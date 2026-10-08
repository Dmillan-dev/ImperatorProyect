set -eu
section() { printf '\nSECTION %s\n' "$1"; }
section identity
id
cat /etc/os-release
section inventory
dpkg-query -W '--showformat=${binary:Package}\t${Version}\t${source:Package}\t${source:Version}\n'
section affected-package-files
for p in zlib1g libsystemd0 libudev1 libxml2 libsqlite3-0 libldap-2.5-0 perl-base perl-modules-5.36 ncurses-bin libtinfo6 libacl1 util-linux mount gzip; do
 printf 'PACKAGE %s\n' "$p"
 dpkg-query -L "$p" 2>/dev/null || true
done
section binaries-and-links
for c in node postgres perl python python3 infocmp mount nsenter setfacl getfacl gzip homectl systemd-homed sqlite3; do
 printf 'COMMAND %s\n' "$c"
 if command -v "$c" >/dev/null 2>&1; then
  command -v "$c"
  case "$c" in node|postgres) ldd "$(command -v "$c")" || true;; esac
 else printf 'ABSENT\n'; fi
done
section specific-components
for p in /usr/lib/systemd/systemd-homed /lib/systemd/systemd-homed /usr/bin/homectl /usr/lib/python3/dist-packages/libxml2.py /usr/lib/postgresql/18/lib/plperl.so /usr/lib/postgresql/18/lib/plpython3.so /usr/share/perl/5.36.0/Archive/Tar.pm; do
 if test -e "$p"; then printf 'PRESENT %s\n' "$p"; else printf 'ABSENT %s\n' "$p"; fi
done
find /usr /app -iname '*minizip*' -o -iname '*libxml2mod*' -o -name 'braces' -o -name 'micromatch' -o -name 'fast-glob' 2>/dev/null || true
section perl
if command -v perl >/dev/null 2>&1; then
 perl -MConfig -e 'print "ptrsize=$Config{ptrsize}; ivsize=$Config{ivsize}\n";'
 for module in Archive::Tar Storable IO::Compress::Gzip; do
  perl -e 'my $m=shift; my $f=$m; $f =~ s!::!/!g; $f.=".pm"; eval {require $f}; if ($@) {print "$m ABSENT\n"} else {no strict "refs"; print "$m PRESENT version=",${$m."::VERSION"}," file=",$INC{$f},"\n"}' "$module"
 done
fi
section fstab-and-suid
cat /etc/fstab 2>/dev/null || true
find /usr/bin /usr/sbin /bin /sbin -type f -perm /6000 -printf '%m %p\n' 2>/dev/null || true
section postgres-init
if command -v postgres >/dev/null 2>&1; then
 postgres --version
 sha256sum /usr/local/bin/docker-entrypoint.sh
 grep -n -E 'gzip|gunzip|bunzip|xzcat|zstd|docker_process_init_files|PGDATA|pg_hba|host|scram' /usr/local/bin/docker-entrypoint.sh || true
fi
section frontend-packages
if test -d /app; then
 find /app -type f -name package.json | sort
 node -e 'for (const m of ["braces","micromatch","fast-glob","eslint","next/dist/compiled/micromatch","next/dist/compiled/glob","next/dist/compiled/picomatch"]) {try {console.log(m,"RESOLVES",require.resolve(m))} catch(e) {console.log(m,"UNRESOLVED",e.code)}}'
 node -e 'const fs=require("fs"),path=require("path");const hits=[]; function walk(p){for (const f of fs.readdirSync(p,{withFileTypes:true})){const q=path.join(p,f.name);if(f.isDirectory())walk(q);else if(f.isFile()&&/\.(?:js|cjs|mjs|json)$/.test(f.name)){const s=fs.readFileSync(q,"utf8");if(s.includes("braces")||s.includes("micromatch")||s.includes("fast-glob"))hits.push({path:q,mentions:["braces","micromatch","fast-glob"].filter(x=>s.includes(x)),sha256:require("crypto").createHash("sha256").update(s).digest("hex")});}}} walk("/app");console.log(JSON.stringify(hits));'
fi
