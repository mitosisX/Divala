from django.http import request
from rest_framework import viewsets
from rest_framework import filters
from ..models import Billing, Journey

from .serializers import BillingHyperserializer, JourneyHyperSerializer


class JourneyViewSet(viewsets.ModelViewSet):
    queryset = Journey.objects.all()
    serializer_class = JourneyHyperSerializer
    filterset_fields = ("destination",)
    filter_backends = (filters.SearchFilter,)
    search_fields = (
        "start",
        "destination",
    )

    def perform_create(self, serializer):
        serializer.save(
            car_model=self.request.user.driver.first().model,
            driver=self.request.user.driver.first(),
        )

    def get_queryset(self):
        return super().get_queryset()


class BillingViewSet(viewsets.ModelViewSet):
    queryset = Billing.objects.all()
    serializer_class = BillingHyperserializer

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

    def get_queryset(self):
        return super().get_queryset()


class DriverView(viewsets.ModelViewSet):
    queryset = Journey.objects.all()
    serializer_class = JourneyHyperSerializer

    def get_queryset(self):
        queryset = self.request.user.driver.first().journeys.all()
        return queryset
