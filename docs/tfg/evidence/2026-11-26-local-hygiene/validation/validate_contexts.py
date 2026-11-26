from pathlib import Path
import json,subprocess
root=Path('D:/Imperator/build/worktrees/tfg-local-hygiene-20261126');out=Path(__file__).parent
cases=[('root','.dockerignore','frontend'),('frontend','infra/docker/frontend/Dockerfile.dockerignore','frontend'),('frontend-directory','frontend/.dockerignore','src'),('backend','infra/docker/backend/Dockerfile.dockerignore','backend-java/src/main/resources'),('migration','infra/aws/images/migration/Dockerfile.dockerignore','database/migrations'),('postgres','infra/docker/postgresql/Dockerfile.dockerignore','')]
private=['local.pem','local.key','local.p8','local.pfx','local.p12','local.jks','local.keystore','local.ppk','id_rsa','id_rsa.pub','id_ecdsa','id_ed25519','credentials','credentials.local','.npmrc','.pypirc','.env','.env.local','.aws/credentials','.codex/state.json','.agents/state.json','.terraform/state','state.tfstate','state.tfstate.backup','local.tfvars','local.tfplan']
results=[]
for name,filterpath,prefix in cases:
 ctx=out/'context-tests'/name;ctx.mkdir(parents=True,exist_ok=False)
 (ctx/'Check.Dockerfile').write_text('FROM scratch\nCOPY . /ctx\n')
 (ctx/'Check.Dockerfile.dockerignore').write_bytes((root/filterpath).read_bytes())
 keep=['frontend/package.json','frontend/package-lock.json','frontend/features/keep.tsx'] if name=='frontend' else ['pom.xml','mvnw','.mvn/wrapper/maven-wrapper.properties','backend-java/src/main/java/Keep.java','infra/docker/backend/entrypoint.sh'] if name=='backend' else ['database/migrations/V1__keep.sql','infra/docker/flyway/entrypoint.sh'] if name=='migration' else ['Dockerfile','Dockerfile.dockerignore','README.md','provision-app-role.sh','supply-chain-lock.json'] if name=='postgres' else ['src/keep.ts','package.json'] if name=='frontend-directory' else ['frontend/package.json','.env.example','README.md']
 secrets=[str(Path(prefix)/p).replace('\\','/') for p in private]+(['build/local.bin','output/tfg/large.tar'] if name=='root' else [])
 for p in keep+secrets:
  f=ctx/p;f.parent.mkdir(parents=True,exist_ok=True);f.write_text('HARMLESS CONTEXT TEST SENTINEL\n')
 dest=out/'context-exports'/name;log=out/(name+'-context-build.log')
 cmd=['docker','buildx','build','--network','none','--progress','plain','--output','type=local,dest='+dest.as_posix(),'-f',str(ctx/'Check.Dockerfile'),str(ctx)]
 with log.open('wb') as f:r=subprocess.run(cmd,stdout=f,stderr=subprocess.STDOUT)
 assert r.returncode==0,(name,log)
 missing=[p for p in keep if not (dest/'ctx'/p).is_file()];leaked=[p for p in secrets if (dest/'ctx'/p).exists()]
 assert not missing and not leaked,(name,missing,leaked)
 results.append(dict(filter=filterpath,result='PASS',requiredInputsKept=keep,privateSentinelsExcluded=secrets,command=cmd,containerRun=False,imageLoaded=False))
 print(name+' context PASS',flush=True)
(out/'docker-context-validation.json').write_text(json.dumps(dict(result='PASS',cases=results),indent=2)+'\n')
