# Generated by Django 3.1.4 on 2021-04-06 18:47

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('users', '0005_auto_20210406_2047'),
    ]

    operations = [
        migrations.AlterField(
            model_name='userdata',
            name='date_of_birth',
            field=models.CharField(max_length=100),
        ),
    ]