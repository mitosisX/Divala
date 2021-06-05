from django.db import models
from ..users.models import User


class Driver(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="driver")
    """A drivers Age should not be over 100 years and below 18 years"""
    number_plate = models.CharField(max_length=100)
    model = models.CharField(max_length=100)


class Car(models.Model):
    driver = models.ForeignKey(Driver, on_delete=models.CASCADE, related_name="car")
    model = models.CharField(max_length=100)
    number_plate = models.CharField(max_length=100)
    number_of_seats = models.IntegerField()

    def __str__(self) -> str:
        return self.model
