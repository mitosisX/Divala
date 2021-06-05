from rest_framework import serializers
from ...users.models import User
from ..models import Driver, Car


class DriverHyperserializer(serializers.HyperlinkedModelSerializer):
    url = serializers.HyperlinkedIdentityField(view_name="drivers-detail")
    user = serializers.HyperlinkedRelatedField(
        queryset=User.objects.all(),
        view_name="users-detail",
        allow_null=True,
        read_only=False,
        required=False,
    )

    class Meta:
        model = Driver
        fields = ["url", "user", "number_plate", "model"]


class CarHyperHyperSerializer(serializers.HyperlinkedModelSerializer):
    url = serializers.HyperlinkedIdentityField(view_name="cars-detail")
    driver = serializers.HyperlinkedRelatedField(
        queryset=Driver.objects.all(),
        view_name="drivers-detail",
        allow_null=True,
        read_only=False,
        required=False,
    )

    class Meta:
        model = Car
        fields = ["url", "driver", "number_plate", "number_of_seats"]
