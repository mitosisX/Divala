from django.db import models
from django.db.models.signals import post_save
from ..users.models import User
from django.core.mail import send_mail
from django.conf import settings


class Journey(models.Model):
    """District names in Malawi are less than 20 characters"""

    driver = models.ForeignKey(
        "drivers.Driver", on_delete=models.CASCADE, related_name="journeys"
    )
    start = models.CharField(max_length=20)
    destination = models.CharField(max_length=20)
    number_of_seats_available = models.IntegerField()
    price = models.FloatField()
    car_model = models.CharField(max_length=1000)
    is_full = models.BooleanField(default=False)

    def __str__(self) -> str:
        return f"{self.start} to {self.destination}"

    def route(self):
        return f"{self.start} to {self.destination}"


class Billing(models.Model):

    user = models.ForeignKey(User, related_name="billing", on_delete=models.CASCADE)
    journey = models.ForeignKey(
        Journey, related_name="billing", on_delete=models.CASCADE
    )


def reduce_journey(sender, instance, **kwargs):
    print("Am alive")
    if instance.journey.number_of_seats_available > 0:
        new_number_of_seats = instance.journey.number_of_seats_available - 1
        instance.journey.number_of_seats_available = new_number_of_seats
        if new_number_of_seats == 0:
            instance.journey.is_full = True
            send_mail(
                subject="Car fully booked",
                message="Your Journey has been fully booked",
                from_email="symonmwenex@gmail.com",
                recipient_list=["symonmwenex@gmail.com"],
            )
        instance.journey.save()


post_save.connect(reduce_journey, sender=Billing)