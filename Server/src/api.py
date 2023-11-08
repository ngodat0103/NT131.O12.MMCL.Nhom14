from flask import abort, redirect, url_for, Flask, request

app = Flask(__name__)

# app.run()
from handle_types_message_client_module import *


@app.route('/authentication', methods=['POST'])
def login():
    return authentication(request.form.to_dict())


@app.route('/update_temp', methods=['POST'])
def update():
    return update_temp(request.form.to_dict())


@app.route("/registration", methods=['POST'])
def registration():
    return create_account(request.form.to_dict())


@app.route("/current_temp", methods=['GET'])
def current_temp():
    return get_temp()
