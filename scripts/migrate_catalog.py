#!/usr/bin/env python3
"""Copy categories and products from one running copy of the store to another.

Works across databases (MySQL -> Postgres) because it only uses the REST API.
Needs Python 3.8+, no extra packages.

  python scripts/migrate_catalog.py \
      --source http://localhost:8080 \
      --target https://padmavathi-store.onrender.com \
      --admin-user admin --admin-password 'YOUR_RENDER_ADMIN_PASSWORD'

Run it once. Re-running skips categories that already exist (matched by name)
and products that already exist in the same category with the same name and size.
"""
import argparse
import json
import sys
import urllib.error
import urllib.request


def call(method, url, body=None, token=None):
    data = json.dumps(body).encode() if body is not None else None
    req = urllib.request.Request(url, data=data, method=method)
    req.add_header("Content-Type", "application/json")
    if token:
        req.add_header("Authorization", "Bearer " + token)
    try:
        with urllib.request.urlopen(req, timeout=120) as r:
            text = r.read().decode()
            return json.loads(text) if text else None
    except urllib.error.HTTPError as e:
        sys.exit(f"{method} {url} failed: {e.code} {e.read().decode()[:300]}")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--source", required=True)
    ap.add_argument("--target", required=True)
    ap.add_argument("--admin-user", default="admin")
    ap.add_argument("--admin-password", required=True)
    a = ap.parse_args()
    src, dst = a.source.rstrip("/"), a.target.rstrip("/")

    print("Reading source catalog...")
    src_cats = call("GET", src + "/api/categories")
    src_prods = call("GET", src + "/api/products")

    print("Logging in to target (the first request may take ~1 min if it was asleep)...")
    token = call("POST", dst + "/api/auth/login",
                 {"username": a.admin_user, "password": a.admin_password})["token"]

    dst_cats = call("GET", dst + "/api/categories")
    by_name = {c["name"]: c["id"] for c in dst_cats}
    cat_map = {}
    for c in src_cats:
        if c["name"] in by_name:
            cat_map[c["id"]] = by_name[c["name"]]
        else:
            created = call("POST", dst + "/api/categories", {"name": c["name"]}, token)
            cat_map[c["id"]] = created["id"]
            print("  + category", c["name"])

    existing = {
        (p["category"]["id"] if p.get("category") else None, p["name"], p.get("size") or "")
        for p in call("GET", dst + "/api/products")
    }
    added = skipped = 0
    for p in src_prods:
        old_cat = p["category"]["id"] if p.get("category") else None
        new_cat = cat_map.get(old_cat)
        if new_cat is None:
            print("  ! skipped (no category):", p["name"])
            continue
        key = (new_cat, p["name"], p.get("size") or "")
        if key in existing:
            skipped += 1
            continue
        call("POST", dst + "/api/products", {
            "name": p["name"],
            "description": p.get("description"),
            "price": p["price"],
            "imageUrl": p.get("imageUrl"),
            "size": p.get("size"),
            "category": {"id": new_cat},
        }, token)
        added += 1
    print(f"Done. {added} products added, {skipped} already there.")


if __name__ == "__main__":
    main()
