#!/usr/bin/env python3
"""Audit the engine with explicit outcomes; scanner failures are never reported as success."""
import argparse
import json
import os
import pathlib
import subprocess

# Scoped exceptions: Known advisories with documented mitigations or non-critical exposure.
SCOPED_EXCEPTIONS = {
    # Tor RSA signature verification, not private-key decryption.
    "RUSTSEC-2023-0071": "Tor signature-verification use; not private-key decryption",
    # Upstream C SQLite advisory via libsqlite3-sys
    "RUSTSEC-2026-0459": "libsqlite3-sys CVE-2026-119375 scoped exception",
}

# Lockfile-only findings: cargo-audit scans Cargo.lock, which also lists packages of optional
# features that are never compiled.
LOCKFILE_ONLY = {
    "RUSTSEC-2026-0387": ("crypto-bigint", "0.5.5"),
}


def not_in_build_graph(crate, name, version):
    features = os.environ.get("AETHER_FEATURES", "tor")
    cmd = [
        "cargo", "tree", "--locked", "--target", "all", "-e", "normal,build",
        "-i", f"{name}@{version}"
    ]
    if features:
        cmd += ["--features", features]
    result = subprocess.run(cmd, cwd=crate, capture_output=True, text=True)
    return result.stdout.strip() == ""


def emit(text):
    print(text, flush=True)
    summary = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary:
        with open(summary, "a", encoding="utf-8") as out:
            out.write(text + "\n")


def audit(crate, report, strict):
    if not (crate / "Cargo.lock").is_file():
        raise RuntimeError(f"Missing engine Cargo.lock: {crate}")
    report.parent.mkdir(parents=True, exist_ok=True)

    ignores = list(SCOPED_EXCEPTIONS.keys())

    # Optional dynamic whitelist via environment variable (e.g. AUDIT_IGNORE="RUSTSEC-XXXX-YYYY,RUSTSEC-...")
    extra_ignores = os.environ.get("AETHER_AUDIT_IGNORE", "").strip()
    if extra_ignores:
        for extra in extra_ignores.split(","):
            extra = extra.strip()
            if extra and extra not in ignores:
                ignores.append(extra)

    for advisory_id, (name, version) in LOCKFILE_ONLY.items():
        if not_in_build_graph(crate, name, version):
            ignores.append(advisory_id)
            emit(f"Lockfile-only exception verified: {name} {version} is not in the build graph ({advisory_id}).")
        else:
            emit(f"::warning::{name} {version} is in the build graph; {advisory_id} is NOT excepted.")

    ignore_args = [arg for advisory_id in set(ignores) for arg in ("--ignore", advisory_id)]
    result = subprocess.run(
        ["cargo", "audit", "--json", *ignore_args],
        cwd=crate,
        capture_output=True,
        text=True,
    )
    report.write_text(result.stdout, encoding="utf-8")
    report.with_suffix(".stderr.log").write_text(result.stderr, encoding="utf-8")

    if result.returncode not in (0, 1):
        raise RuntimeError(f"cargo audit scanner failed (exit {result.returncode}); see {report.with_suffix('.stderr.log')}")

    try:
        data = json.loads(result.stdout)
        vulnerabilities = data.get("vulnerabilities", {}).get("list", [])
        warnings = data.get("warnings", {})
        if not isinstance(vulnerabilities, list) or not isinstance(warnings, dict):
            raise ValueError("Invalid report collections")
        if data.get("vulnerabilities", {}).get("count") != len(vulnerabilities):
            raise ValueError("Inconsistent vulnerability count")
        if data.get("vulnerabilities", {}).get("found") != bool(vulnerabilities):
            raise ValueError("Inconsistent vulnerability status")
        if "database" not in data or "lockfile" not in data:
            raise ValueError("Incomplete scanner report")
        if result.returncode == 1 and not vulnerabilities:
            raise ValueError("Scanner exit 1 without vulnerability findings")
    except (ValueError, KeyError, TypeError) as error:
        raise RuntimeError(f"Unusable cargo audit JSON: {error}") from error

    emit("### Rust dependency audit")
    emit(f"Vulnerabilities: **{len(vulnerabilities)}**; informational warnings: **{sum(len(v) for v in warnings.values())}**.")
    for adv_id, reason in SCOPED_EXCEPTIONS.items():
        emit(f"Existing scoped exception: `{adv_id}` ({reason}).")

    for kind, items in [("vulnerability", vulnerabilities), *warnings.items()]:
        for item in items:
            package = item["package"]
            advisory = item.get("advisory") or {}
            text = (f"{kind}: {package['name']} {package['version']} — "
                    f"{advisory.get('id', 'no advisory ID')}: {advisory.get('title', kind)}")
            emit("- " + text)
            escaped = text.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")
            print(f"::warning::{escaped}", flush=True)

    emit(f"Scanner completed (exit {result.returncode}). JSON report: `{report.name}`.")
    if vulnerabilities:
        emit("Unresolved vulnerabilities block release." if strict else "Advisory-only policy: findings are reported, not a build failure.")

    return 1 if vulnerabilities and strict else 0


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--crate", type=pathlib.Path, default=pathlib.Path("native/aether/aether"))
    parser.add_argument("--report", type=pathlib.Path, required=True)
    parser.add_argument("--strict", action="store_true", help="Fail on unresolved vulnerabilities, not maintenance warnings")
    args = parser.parse_args()
    try:
        code = audit(args.crate.resolve(), args.report.resolve(), args.strict)
    except (OSError, RuntimeError, KeyError, TypeError) as error:
        emit(f"::error::Rust audit did not complete: {error}")
        code = 2
    raise SystemExit(code)