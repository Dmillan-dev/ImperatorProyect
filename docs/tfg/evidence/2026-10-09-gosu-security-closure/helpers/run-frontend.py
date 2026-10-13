import pathlib,subprocess,os,json,datetime,sys
sys.stdout.reconfigure(encoding="utf8")
f=pathlib.Path("D:/Imperator/build/tfg-a/go1269-2026-10-09/frontend-validation/frontend"); b=f.parents[1]
env=dict(os.environ,NEXT_TELEMETRY_DISABLED="1"); npm=[env.get("COMSPEC","C:/Windows/System32/cmd.exe"),"/d","/c","npm.cmd"]
results=[]
for check,args in [("npm-ci",["ci","--no-audit","--no-fund"]),("tests",["run","test"]),("lint",["run","lint"]),("types",["run","typecheck"]),("build",["run","build"]),("format",["run","format:check"]),("npm-audit-production",["audit","--omit=dev","--json"]),("npm-audit-full",["audit","--json"])]:
 start=datetime.datetime.now(datetime.timezone.utc).isoformat()
 with (b/("frontend-"+check+".log")).open("w",encoding="utf8") as log: code=subprocess.run(npm+args,cwd=f,env=env,stdout=log,stderr=subprocess.STDOUT,timeout=900).returncode
 results.append(dict(check=check,command=npm+args,exitCode=code,startedAtUtc=start,finishedAtUtc=datetime.datetime.now(datetime.timezone.utc).isoformat(),sourceSha="a931601102ee19a90b3dad8477408ac0d1854669"))
 (b/"frontend-results.json").write_text(json.dumps(results,indent=2)+"\n",encoding="utf8"); print(check,code,flush=True)
 if code and check not in ["npm-audit-full"]: break
