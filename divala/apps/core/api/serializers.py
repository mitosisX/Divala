from django.db import models
from rest_framework import serializers
from ..models import Journey, Billing
from ...drivers.models import Driver
from ...users.models import User
from django.conf import settings


class JourneyHyperSerializer(serializers.HyperlinkedModelSerializer):
    driver = serializers.HyperlinkedRelatedField(
        queryset=Driver.objects.all(),
        view_name="drivers-detail",
        allow_null=True,
        required=False,
    )
    url = serializers.HyperlinkedIdentityField(view_name="journeys-detail")
    is_booked = serializers.SerializerMethodField()

    class Meta:
        model = Journey
        fields = [
            "url",
            "driver",
            "start",
            "destination",
            "number_of_seats_available",
            "price",
            "car_model",
            "route",
            "is_full",
            "is_booked",
        ]
        extra_kwargs = {
            "car_model": {"required": False},
            "is_full": {"required": False},
        }

    def get_is_booked(self, obj):
        if obj.billing.filter(user=self.context["request"].user.id):
            return True
        else:
            return False


class BillingHyperserializer(serializers.HyperlinkedModelSerializer):
    user = serializers.HyperlinkedRelatedField(
        queryset=User.objects.all(), view_name="users-detail", required=False
    )
    journey = serializers.HyperlinkedRelatedField(
        queryset=Journey.objects.all(),
        view_name="journeys-detail",
        many=False,
        required=True,
    )
    url = serializers.HyperlinkedIdentityField(view_name="billings-detail")

    class Meta:
        model = Billing
        fields = ["url", "user", "journey"]