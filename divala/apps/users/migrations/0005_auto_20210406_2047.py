# Generated by Django 3.1.4 on 2021-04-06 18:47

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('users', '0004_auto_20210404_1457'),
    ]

    operations = [
        migrations.AlterField(
            model_name='userdata',
            name='date_of_birth',
            field=models.CharField(max_length=1000000),
        ),
        migrations.AlterField(
            model_name='userdata',
            name='national_id_image',
            field=models.TextField(),
        ),
    ]
