import pathlib,subprocess,json,os,datetime,hashlib,shutil,sys
sys.stdout.reconfigure(encoding="utf8")
b=pathlib.Path("D:/Imperator/build/tfg-a/go1269-2026-10-09"); out=b/"scans"; out.mkdir(exist_ok=True); cache=b/"trivy-cache"; w=pathlib.Path("D:/Imperator/build/worktrees/tfg-a-go1269-20261009"); old=pathlib.Path("D:/Imperator/build/tfg-a/exact-security-2026-10-09")
def read(p): return json.loads(p.read_text(encoding="utf-8-sig"))
def save(p,v): p.write_text(json.dumps(v,indent=2,ensure_ascii=False)+"\n",encoding="utf8")
images=read(old/"preflight-images.json")["images"]; pg=read(w/"build/go1269-d094/repeat/provenance.json")
for im in images:
 if im["component"]=="postgres": im.update(indexDigest=pg["imageIndexDigest"],platformManifestDigest=pg["runtimeManifestDigest"],sourceRevision=pg["sourceRevision"],scanReference="imperator/postgres@"+pg["imageIndexDigest"])
trivy=shutil.which("trivy"); env={k:v for k,v in os.environ.items() if not k.startswith("TRIVY_")}; commands=[]; results=[]
def run(args,label):
 start=datetime.datetime.now(datetime.timezone.utc).isoformat()
 with (out/(label+".log")).open("w",encoding="utf8") as log: code=subprocess.run(args,cwd=w,env=env,stdout=log,stderr=subprocess.STDOUT,timeout=900).returncode
 commands.append(dict(command=args,label=label,startedAtUtc=start,finishedAtUtc=datetime.datetime.now(datetime.timezone.utc).isoformat(),exitCode=code)); save(out/"scan-commands.json",commands); return code
for check in ["download-db-only","download-java-db-only"]:
 assert run([trivy,"--cache-dir",str(cache),"--config","","image","--"+check,"--no-progress"],check)==0
save(out/"scanner-databases.json",{name:read(cache/folder/"metadata.json") for name,folder in [("vulnerability","db"),("java","java-db")]})
save(out/"scanner-tool.json",dict(executable=trivy,version=subprocess.check_output([trivy,"--version"],env=dict(env,TRIVY_CACHE_DIR=str(cache)),text=True),sha256=hashlib.sha256(pathlib.Path(trivy).read_bytes()).hexdigest(),upgradedByAgent=False))
for im in images:
 c=im["component"]; inspections=json.loads(subprocess.check_output(["docker","image","inspect","--platform","linux/amd64",im["scanReference"]]))[0]; assert inspections["Id"]==im["platformManifestDigest"]
 common=[trivy,"--cache-dir",str(cache),"--config","","--timeout","10m","image","--image-src","docker","--platform","linux/amd64","--skip-db-update","--skip-java-db-update","--skip-version-check","--disable-telemetry","--no-progress","--ignorefile",""]
 for kind,flags in [("vulnerabilities",["--scanners","vuln","--severity","UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL","--exit-code","0"]),("fixable-gate",["--scanners","vuln","--severity","HIGH,CRITICAL","--ignore-unfixed","--exit-code","1"]),("credentials",["--scanners","secret","--severity","UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL","--image-config-scanners","secret","--secret-config","","--exit-code","1"])]:
  label=c+"-"+kind; report=out/(label+".json"); code=run(common+flags+["--format","json","--output",str(report),im["scanReference"]],label); assert code in [0,1],label
  r=read(report); assert r["Metadata"]["ImageID"]==im["indexDigest"] and r["Metadata"]["DiffIDs"]==inspections["RootFS"]["Layers"]
 full=read(out/(c+"-vulnerabilities.json")); vs=[v for r in full.get("Results",[]) for v in r.get("Vulnerabilities",[]) if v["Severity"] in ["HIGH","CRITICAL"]]; ss=read(out/(c+"-credentials.json")); secrets=sum(len(r.get("Secrets",[])) for r in ss.get("Results",[])); fixed=[v for v in vs if v.get("FixedVersion")]
 results.append(dict(component=c,indexDigest=im["indexDigest"],platformManifestDigest=im["platformManifestDigest"],sourceRevision=im["sourceRevision"],fixableHighCritical=len(fixed),unfixedHighCritical=len(vs)-len(fixed),uniqueHCCVEs=len(set(v["VulnerabilityID"] for v in vs)),secrets=secrets,fixableGateExit=commands[-2]["exitCode"],secretGateExit=commands[-1]["exitCode"],rootfs=inspections["RootFS"],scanReference=im["scanReference"])); save(out/"scan-summary.json",results); print(c,"fixableHC",len(fixed),"secrets",secrets,flush=True)
save(out/"strict-gate.json",dict(result="PASS" if all(r["fixableHighCritical"]==0 and r["secrets"]==0 and r["fixableGateExit"]==0 and r["secretGateExit"]==0 for r in results) else "NO-GO",accepted=False,riskAcceptanceIndependent=True,images=results)); print("FULL FOUR-IMAGE SCAN COMPLETE",flush=True)
