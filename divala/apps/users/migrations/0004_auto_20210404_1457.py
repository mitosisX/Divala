# Generated by Django 3.1.4 on 2021-04-04 12:57

from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('users', '0003_auto_20210321_1000'),
    ]

    operations = [
        migrations.AlterField(
            model_name='userdata',
            name='national_id_image',
            field=models.CharField(max_length=1000000),
        ),
        migrations.AlterField(
            model_name='userdata',
            name='user',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='user_data', to=settings.AUTH_USER_MODEL),
        ),
    ]
