import os
import sys
import zipfile


def should_skip(path_parts):
    skipped = {".git", ".gitignore", "README.md"}
    return any(part in skipped for part in path_parts)


def main():
    if len(sys.argv) != 3:
        print("Usage: package_lambda.py <source_dir> <output_zip>", file=sys.stderr)
        return 1

    source_dir = os.path.abspath(sys.argv[1])
    output_zip = os.path.abspath(sys.argv[2])
    os.makedirs(os.path.dirname(output_zip), exist_ok=True)

    if os.path.exists(output_zip):
        os.remove(output_zip)

    with zipfile.ZipFile(output_zip, "w", zipfile.ZIP_DEFLATED) as archive:
        for root, _, files in os.walk(source_dir):
            rel_root = os.path.relpath(root, source_dir)
            parts = [] if rel_root == "." else rel_root.split(os.sep)
            if should_skip(parts):
                continue
            for filename in files:
                full_path = os.path.join(root, filename)
                rel_path = os.path.relpath(full_path, source_dir)
                if should_skip(rel_path.split(os.sep)):
                    continue
                archive.write(full_path, rel_path.replace(os.sep, "/"))

    print(output_zip)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
