from rest_framework import viewsets
from rest_framework import response
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.authtoken.models import Token
from rest_framework.generics import CreateAPIView
from rest_framework.response import Response
from rest_framework.authentication import TokenAuthentication
from .permissions import UpdateOwnProfile
from .serializers import UserDataHyperSerialiser, UsersHyperSerializer
from ..models import User, UserData


class UserHyperViewSet(viewsets.ModelViewSet):
    """handles creating and updating profiles"""

    serializer_class = UsersHyperSerializer
    queryset = User.objects.all()
    authentication_classes = (TokenAuthentication,)
    permission_classes = (UpdateOwnProfile,)

    def create(self, request, *args, **kwargs):
        response = super().create(request, *args, **kwargs)
        token, created = Token.objects.get_or_create(user_id=response.data["id"])
        response.data["token"] = str(token)
        return response


class UserDataView(viewsets.ModelViewSet):
    serializer_class = UserDataHyperSerialiser
    queryset = UserData.objects.all()

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)


class MyCustomToken(ObtainAuthToken):
    def post(self, request, *args, **kwargs):
        driver = False
        serializer = self.serializer_class(
            data=request.data, context={"request": request}
        )
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data["user"]
        token, created = Token.objects.get_or_create(user=user)
        if user.driver.exists():
            driver = True
        return Response(
            {
                "token": token.key,
                "user_id": user.pk,
                "name": user.name,
                "email": user.email,
                "is_driver": driver,
                "base_64": user.base_64,
            }
        )
