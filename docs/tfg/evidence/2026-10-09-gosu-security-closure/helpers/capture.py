from pathlib import Path
import json, subprocess, base64, time, uuid, os
from cryptography.hazmat.primitives import serialization, hashes
from cryptography.hazmat.primitives.asymmetric import padding

root = Path('D:/Imperator/build/tfg-a/go1269-2026-10-09/frontend-validation')
out = Path('D:/Imperator/build/worktrees/tfg-a-go1269-20261009/build/go1269-runtime')
key = serialization.load_pem_private_key((out / 'private/fixture.key').read_bytes(), password=None)
identity_path = out / 'demo-identifiers.json'
if identity_path.exists():
    identity = json.loads(identity_path.read_text())
    replay = True
else:
    identity = dict(decisionId=str(uuid.uuid4()), recommendationId=str(uuid.uuid4()),
                    actors={role: str(uuid.uuid4()) for role in ['ADMIN', 'PLATFORM_ENGINEER', 'FINANCE', 'AUDITOR']})
    identity_path.write_text(json.dumps(identity, indent=2) + '\n')
    replay = False
def encode(value):
    return base64.urlsafe_b64encode(json.dumps(value, separators=(',', ':')).encode()).decode().rstrip('=')
def token(role):
    now = int(time.time())
    content = encode(dict(alg='RS256', kid='tfg-local-only', typ='JWT')) + '.' + encode(dict(iss='https://tfg-jwks:8443',
              aud=['imperator-api'], sub=identity['actors'][role], imperator_role=role, iat=now, nbf=now-5, exp=now+900))
    signature = key.sign(content.encode(), padding.PKCS1v15(), hashes.SHA256())
    return content + '.' + base64.urlsafe_b64encode(signature).decode().rstrip('=')
if __name__ == '__main__':
    config = dict(**identity, origin='http://127.0.0.1:3011', outputDirectory='build/go1269-runtime-capture',
                  replay=replay, tokens={role: token(role) for role in identity['actors']})
    with (out / 'capture.log').open('w', encoding='utf-8') as log:
        code = subprocess.run(['node', 'scripts/tfg/capture-docker-demo.mjs'], input=json.dumps(config), cwd=root, env=dict(os.environ, PLAYWRIGHT_BROWSERS_PATH='D:/Imperator/build/tfg-a/go1269-2026-10-09/playwright-browsers', TFG_BROWSER_CHANNEL='chromium'),
                              stdout=log, stderr=subprocess.STDOUT, text=True, timeout=300).returncode
    print('Docker capture exit:', code)
    print((out / 'capture.log').read_text()[-3500:])
    raise SystemExit(code)

