from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate 
from flask_marshmallow import Marshmallow
from flask_restful import Api

app = Flask(__name__)

app.config.from_object('config')
db = SQLAlchemy(app)

ma = Marshmallow(app)

migrate = Migrate(app=app, db=db)

api = Api(app)

from .models import account_model, operations_model

from .views import account_view, operation_view
