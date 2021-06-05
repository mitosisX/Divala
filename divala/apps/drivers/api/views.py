from rest_framework import viewsets
from ..models import Car, Driver
from .serializers import (
    CarHyperHyperSerializer,
    DriverHyperserializer,
)


class DriverHyperViewset(viewsets.ModelViewSet):
    queryset = Driver.objects.all()
    serializer_class = DriverHyperserializer

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

    def get_queryset(self):
        user_queryset = self.queryset.filter(user=self.request.user)
        return user_queryset


class CarHyperViewset(viewsets.ModelViewSet):
    queryset = Car.objects.all()
    serializer_class = CarHyperHyperSerializer

    def perform_create(self, serializer):
        serializer.save(driver=self.request.user.driver.first())

    def get_queryset(self):
        user_queryset = self.queryset.filter(driver=self.request.user.driver.first())
        return user_queryset