from gevent.pywsgi import WSGIServer
from api import app

http_server = WSGIServer(("0.0.0.0", 80), app)
http_server.serve_forever()
