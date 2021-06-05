from rest_framework import serializers
from ..models import User, UserData


class UsersHyperSerializer(serializers.HyperlinkedModelSerializer):
    url = serializers.HyperlinkedIdentityField(view_name="users-detail")

    class Meta:
        model = User
        fields = (
            "id",
            "url",
            "email",
            "national_id_number",
            "base_64",
            "name",
            "password",
        )
        extra_kwargs = {"password": {"write_only": True}}

    def create(self, validated_data):
        user = User(
            email=validated_data["email"],
            name=validated_data["name"],
            national_id_number=validated_data["national_id_number"],
            base_64=validated_data["base_64"],
        )
        user.set_password(validated_data["password"])
        user.save()
        return user


class UserDataHyperSerialiser(serializers.ModelSerializer):
    class Meta:
        model = UserData
        fields = (
            "national_id_image",
            "date_of_birth",
            "location",
            "phone_number",
        )
