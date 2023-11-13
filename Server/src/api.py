from flask import abort, redirect, url_for, Flask, request

app = Flask(__name__)

# app.run()
from handle_types_message_client_module import *


@app.route('/authentication', methods=['POST'])
def login():
    return authentication(request.form.to_dict())


@app.route("/registration", methods=['POST'])
def registration():
    return create_account(request.form.to_dict())


@app.route("/reset_password", methods=['POST'])
def reset_password():
    if request.headers.get("projects") == "mobile":
        if request.headers.get("check-valid-otp") == "false":
            return forgot_password_mobile(request.form.get("email"), False)
        else:
            return forgot_password(request.form.get("email"), True, request.form.get("otp_code"))
    elif request.headers.get("projects") == "nhung" and request.headers.get("change-password") == "false":
        if request.headers.get("check-valid-otp") == "false":
            return forgot_password(request.form.to_dict(), False)
        else:
            return forgot_password(request.form.to_dict(), True)
    elif request.headers.get("projects") == "nhung" and request.headers.get("change-password") == "true":
        return change_password(request.form.to_dict())
    else:
        abort(400)


@app.route('/update_temp', methods=['POST'])
def update():
    return update_temp(request.form.to_dict())


@app.route("/current_temp", methods=['GET'])
def current_temp():
    return get_temp()
