from django.urls import path, include
from rest_framework.routers import DefaultRouter

# views
from apps.core.api.views import JourneyViewSet, BillingViewSet, DriverView
from apps.drivers.api.views import (
    DriverHyperViewset,
    CarHyperViewset,
)
from apps.users.api.views import MyCustomToken, UserDataView, UserHyperViewSet


router = DefaultRouter()

router.register(r"journeys", JourneyViewSet, basename="journeys")
router.register(r"billings", BillingViewSet, basename="billings")
router.register(r"users", UserHyperViewSet, basename="users")
router.register(r"drivers", DriverHyperViewset, basename="drivers")
router.register(r"cars", CarHyperViewset, basename="cars")
router.register(r"change", DriverView, basename="change")
router.register(r"data", UserDataView, basename="data")


urlpatterns = [
    path("", include(router.urls)),
    path("token/", MyCustomToken.as_view()),
]
