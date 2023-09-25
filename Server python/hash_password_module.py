import hashlib
def hash_password(plain_password_str:str):
    hash_interpreter = hashlib.sha256()
    password_bytes = plain_password_str.encode()
    hash_interpreter.update(password_bytes)
    hash_password = hash_interpreter.hexdigest()
    return hash_password