from django.urls import path

from config import views

urlpatterns = [
    path("", views.home),
    path("health/live/", views.live),
    path("health/ready/", views.ready),
    path("api/ping/", views.ping),
]
