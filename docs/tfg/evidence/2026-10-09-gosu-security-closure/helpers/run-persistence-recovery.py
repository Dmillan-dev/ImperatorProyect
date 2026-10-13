import pathlib,subprocess,json,urllib.request,time,re,datetime,sys
from capture import token,identity
sys.stdout.reconfigure(encoding="utf8")
w=pathlib.Path("D:/Imperator/build/worktrees/tfg-a-go1269-20261009"); r=w/"build/go1269-runtime"; command=json.loads((r/"compose-command.json").read_text()); initial=json.loads((r/"runtime-image-bindings.json").read_text()); origin="http://127.0.0.1:3011"
def read(route):
 request=urllib.request.Request(origin+route,headers={"Authorization":"Bearer "+token("AUDITOR")})
 with urllib.request.urlopen(request,timeout=30) as response: assert response.status==200; return json.load(response)
def run(args,file,timeout=360):
 with (r/file).open("w",encoding="utf8") as log: code=subprocess.run(args,cwd=w,stdout=log,stderr=subprocess.STDOUT,timeout=timeout).returncode
 assert code==0,file
path="/api/v1/decisions/"+identity["decisionId"]; valueRoute="/api/v1/business-value?decisionId="+identity["decisionId"]; before=read(valueRoute); ledger=read(path+"/ledger?sort=occurredAt&direction=ASC")
run(command+["up","--detach","--no-build","--pull","never","--force-recreate","--wait","--wait-timeout","240","postgres","backend","frontend"],"recreation.log")
records={}
for s in ["postgres","backend","frontend","flyway-migrate","flyway-validate","postgres-permissions"]:
 cid=subprocess.check_output(command+["ps","--all","--quiet",s],cwd=w,text=True).strip(); obj=json.loads(subprocess.check_output(["docker","inspect",cid]))[0]; assert obj["Image"]==initial[s]["actualImage"]; image=json.loads(subprocess.check_output(["docker","image","inspect","--platform","linux/amd64",obj["Image"]]))[0]; assert image["RootFS"]==initial[s]["rootfs"]; records[s]=dict(containerId=cid,actualImage=obj["Image"],rootfs=image["RootFS"],state=obj["State"]["Status"],health=obj["State"].get("Health",{}).get("Status"),exitCode=obj["State"]["ExitCode"])
assert all(records[s]["containerId"]!=initial[s]["containerId"] for s in ["postgres","backend","frontend"]); assert before==read(valueRoute); assert ledger==read(path+"/ledger?sort=occurredAt&direction=ASC"); assert ledger["totalItems"]==3
query="SELECT version, success FROM flyway_schema_history ORDER BY installed_rank; SELECT 'evidence', count(*) FROM evidence; SELECT 'ledger', count(*) FROM ledger_entries; SELECT tablename, has_table_privilege('imperator_app', 'public.' || tablename, 'SELECT'), has_table_privilege('imperator_app', 'public.' || tablename, 'INSERT'), has_table_privilege('imperator_app', 'public.' || tablename, 'UPDATE'), has_table_privilege('imperator_app', 'public.' || tablename, 'DELETE') FROM pg_tables WHERE schemaname='public' AND tablename IN ('ledger_entries','recommendation_explanations','recommendation_explanation_evidence','recommendation_explanation_assumptions') ORDER BY tablename;"
sql=subprocess.check_output(["docker","exec",records["postgres"]["containerId"],"psql","-U","imperator_owner","-d","imperator","-Atc",query],text=True); assert "evidence|30" in sql and "ledger|3" in sql
for t in ["ledger_entries","recommendation_explanations","recommendation_explanation_evidence","recommendation_explanation_assumptions"]: assert t+"|t|t|f|f" in sql
result=dict(result="PASS",executedAtUtc=datetime.datetime.now(datetime.timezone.utc).isoformat(),replacedContainers=3,unchangedBusinessValue=True,unchangedLedger=True,evidenceCount=30,ledgerEntries=3,databaseChecks=sql.splitlines(),images=records); (r/"persistence.json").write_text(json.dumps(result,indent=2)+"\n",encoding="utf8"); print("Persistence/V1-V3/append-only permissions: PASS",flush=True)
def status(route):
 req="GET "+route+" HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n"; script="exec 3<>/dev/tcp/127.0.0.1/8080; printf '%b' '"+req+"' >&3; head -n 1 <&3"; response=subprocess.check_output(["docker","exec","--user","10001:10001",records["backend"]["containerId"],"timeout","12","bash","-ec",script],text=True); return int(response.split()[1])
def wait(route,expected):
 for i in range(90):
  try: actual=status(route)
  except (subprocess.CalledProcessError,IndexError): actual=0
  if actual==expected: return actual
  time.sleep(1)
 raise RuntimeError(route+" did not reach "+str(expected))
checks={"healthy":{"readyz":wait("/readyz",200),"livez":wait("/livez",200)}}
try:
 run(command+["stop","postgres"],"readiness-stop-postgres.log",90); checks["postgresStopped"]={"readyz":wait("/readyz",503),"livez":wait("/livez",200)}
finally: run(command+["start","postgres"],"readiness-start-postgres.log",90)
checks["recovered"]={"readyz":wait("/readyz",200),"livez":wait("/livez",200)}; assert before==read(valueRoute); assert ledger==read(path+"/ledger?sort=occurredAt&direction=ASC")
for s in ["postgres","backend","frontend"]:
 cid=subprocess.check_output(command+["ps","--quiet",s],cwd=w,text=True).strip(); obj=json.loads(subprocess.check_output(["docker","inspect",cid]))[0]; assert obj["Image"]==initial[s]["actualImage"]
(r/"readiness-recovery.json").write_text(json.dumps(dict(result="PASS",checks=checks,sameExactImages=True,preservedBusinessValueAndLedger=True,executedAtUtc=datetime.datetime.now(datetime.timezone.utc).isoformat()),indent=2)+"\n",encoding="utf8"); print("Readiness503/liveness200/recovery200: PASS",flush=True)
