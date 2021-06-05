from django.db import models

from django.contrib.auth.models import AbstractBaseUser
from django.contrib.auth.models import PermissionsMixin
from django.contrib.auth.models import BaseUserManager


class UserProfileManager(BaseUserManager):
    """Helps django work with the custom user"""

    def create_user(self, email, name, password=None):
        """creates a new user in the system"""

        if not email:
            raise ValueError("Users Must have an Email address")
        email = self.normalize_email(email)
        user = self.model(email=email, name=name)

        """encripts the user's password"""
        user.set_password(password)
        user.save(using=self._db)

        return user

    def create_superuser(self, email, name, password):
        """creates a new super user with given details"""

        user = self.create_user(email, name, password)

        user.is_superuser = True
        user.is_staff = True

        user.save(using=self._db)


class User(AbstractBaseUser, PermissionsMixin):
    """Represent a user profile inside our system"""

    email = models.EmailField(max_length=255, unique=True)
    name = models.CharField(max_length=255)
    is_active = models.BooleanField(default=True)
    is_staff = models.BooleanField(default=False)

    national_id_number = models.CharField(max_length=100)
    base_64 = models.CharField(max_length=1000000)

    objects = UserProfileManager()

    USERNAME_FIELD = "email"
    REQUIRED_FIELDS = ["name"]

    def get_full_name(self):
        """Used to get a users full name"""

        return self.name

    def get_short_name(self):
        """Used to get a users short name"""

        return self.name

    def __str__(self):
        """converts an object to the string"""

        return self.email


class UserData(models.Model):
    user = models.ForeignKey(User, related_name="user_data", on_delete=models.CASCADE)
    national_id_image = models.TextField()
    date_of_birth = models.CharField(max_length=100)
    location = models.CharField(max_length=100)
    phone_number = models.CharField(max_length=20)