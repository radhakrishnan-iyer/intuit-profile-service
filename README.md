# intuit-profile-service
Profile service responsible to perform CRUD operation on Profile

This is the profile service which will perform the CRUD operations on profile.
It does it asyncrhonously using RabbitMQ in between this profile service and subscription-service

VM args -
java -Dlogsdir=<log_path> -jar <base_path>\intuit-profile-service\target\intuit-profile-service-1.0.0-SNAPSHOT.jar
