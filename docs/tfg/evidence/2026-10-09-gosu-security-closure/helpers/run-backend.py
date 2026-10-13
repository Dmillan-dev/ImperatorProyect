import pathlib,subprocess,json,os,datetime,secrets,time,re,sys,xml.etree.ElementTree as ET
sys.stdout.reconfigure(encoding="utf8")
w=pathlib.Path("D:/Imperator/build/worktrees/tfg-a-go1269-20261009"); b=pathlib.Path("D:/Imperator/build/tfg-a/go1269-2026-10-09"); out=b/"backend"; out.mkdir(exist_ok=True); private=out/"private"; private.mkdir(exist_ok=True)
owner=secrets.token_hex(24); app=secrets.token_hex(24); (private/"owner-password").write_text(owner,encoding="ascii")
prov=json.loads((w/"build/go1269-d094/repeat/provenance.json").read_text(encoding="utf-8-sig")); image="imperator/postgres@"+prov["imageIndexDigest"]; name="imperator-go1269-integration"; vol="imperator-go1269-integration-data"
env=dict(os.environ,JAVA_HOME="C:/Program Files/Java/jdk-21.0.12",IMPERATOR_IT_DATABASE="imperator_it_tfg_a",IMPERATOR_IT_DB_URL="jdbc:postgresql://127.0.0.1:55439/imperator_it_tfg_a",IMPERATOR_IT_ADMIN_USER="imperator_it_owner",IMPERATOR_IT_ADMIN_PASSWORD=owner,IMPERATOR_IT_APP_USER="imperator_it_tfg_app",IMPERATOR_IT_APP_PASSWORD=app,GIT_OPTIONAL_LOCKS="0")
def run(args,file,timeout=120):
 with (out/file).open("w",encoding="utf8") as log: return subprocess.run(args,cwd=w,env=env,stdout=log,stderr=subprocess.STDOUT,timeout=timeout).returncode
assert not subprocess.check_output(["docker","ps","-aq","--filter","name=^/"+name+"$"],text=True).strip()
assert vol not in subprocess.check_output(["docker","volume","ls","-q"],text=True).splitlines()
subprocess.run(["docker","volume","create","--label","imperator.audit=go1269-20261009",vol],check=True,stdout=subprocess.DEVNULL)
command=["docker","run","--detach","--name",name,"--label","imperator.audit=go1269-20261009","--pull=never","--platform","linux/amd64","--user","999:999","--read-only","--cap-drop","ALL","--cap-add","CHOWN","--cap-add","DAC_OVERRIDE","--cap-add","FOWNER","--cap-add","SETGID","--cap-add","SETUID","--security-opt","no-new-privileges:true","--cpus","1","--memory","1024m","--pids-limit","200","--publish","127.0.0.1:55439:5432","--tmpfs","/tmp:size=16m,mode=1777","--tmpfs","/var/run/postgresql:size=16m,uid=999,gid=999,mode=0775","--mount","type=volume,source="+vol+",target=/var/lib/postgresql","--mount","type=bind,source="+(private/"owner-password").as_posix()+",target=/run/secrets/owner-password,readonly","--env","POSTGRES_PASSWORD_FILE=/run/secrets/owner-password","--env","POSTGRES_USER=imperator_it_owner","--env","POSTGRES_DB=imperator_it_tfg_a","--env","POSTGRES_INITDB_ARGS=--auth-host=scram-sha-256",image]
started=False; code=99; totals={}
try:
 assert run(command,"docker-start.log")==0; started=True
 for i in range(60):
  ready=subprocess.run(["docker","exec",name,"pg_isready","-U","imperator_it_owner","-d","imperator_it_tfg_a"],capture_output=True).returncode
  if ready==0: break
  time.sleep(1)
 else: raise RuntimeError("Integration database not ready")
 mvn="C:/Users/J4GG3R/.m2/wrapper/dists/apache-maven-3.9.16/0daed3be3ebd1c706f0e69e8b07c6b73f5cc4ea3dfce72a8d0ec2e849ca2ddb0/bin/mvn.cmd"; args=[env.get("COMSPEC","C:/Windows/System32/cmd.exe"),"/d","/c",mvn,"--batch-mode","--no-transfer-progress","-Ppostgresql-integration","clean","verify"]
 start=datetime.datetime.now(datetime.timezone.utc).isoformat(); code=run(args,"maven-integration.log",1200)
 for folder in ["surefire-reports","failsafe-reports"]:
  tests=failures=errors=skips=0; destination=out/folder; destination.mkdir(exist_ok=True)
  for p in (w/"target"/folder).glob("*.xml"):
   tree=ET.parse(p).getroot()
   if tree.tag=="testsuite":
    tests+=int(tree.attrib.get("tests",0)); failures+=int(tree.attrib.get("failures",0)); errors+=int(tree.attrib.get("errors",0)); skips+=int(tree.attrib.get("skipped",0))
   safe=p.read_text(encoding="utf8",errors="replace").replace(owner,"[REDACTED]").replace(app,"[REDACTED]"); safe=re.sub(r"eyJ[A-Za-z0-9_-]{10,}\.[A-Za-z0-9_-]{10,}\.[A-Za-z0-9_-]{10,}","[REDACTED_JWT]",safe); (destination/p.name).write_text(safe,encoding="utf8")
  totals[folder]=dict(tests=tests,failures=failures,errors=errors,skips=skips)
finally:
 if started:
  run(["docker","logs",name],"postgres-runtime.log"); run(["docker","stop",name],"docker-stop.log"); run(["docker","rm",name],"docker-remove.log")
 for p in out.glob("*.log"):
  safe=p.read_text(encoding="utf8",errors="replace").replace(owner,"[REDACTED]").replace(app,"[REDACTED]"); safe=re.sub(r"eyJ[A-Za-z0-9_-]{10,}\.[A-Za-z0-9_-]{10,}\.[A-Za-z0-9_-]{10,}","[REDACTED_JWT]",safe); p.write_text(safe,encoding="utf8")
 result=dict(sourceSha=prov["sourceRevision"],imageReference=image,imageIndexDigest=prov["imageIndexDigest"],runtimeManifestDigest=prov["runtimeManifestDigest"],exitCode=code,totals=totals,scope="Synthetic integration harness: same exact PostgreSQL image; temporary loopback JDBC port55439 differs from canonical Compose runtime, which publishes no database port.",existingVolumesUntouched=True,executedAtUtc=datetime.datetime.now(datetime.timezone.utc).isoformat()); (out/"backend-result.json").write_text(json.dumps(result,indent=2)+"\n",encoding="utf8"); print(json.dumps(result,indent=2),flush=True)
