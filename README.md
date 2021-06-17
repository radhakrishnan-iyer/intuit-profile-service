# intuit-profile-service
Profile service responsible to perform CRUD operation on Profile

This is the profile service which will perform the CRUD operations on profile.
It does it asyncrhonously using RabbitMQ in between this profile service and subscription-service.
It sends the request via an Event object to subscription-service over RabbitMQ Queue and receives the response from subscription-service through RabbitMQ topic.

Note : RabbitMQ queue & topic can be replaced by with AWS SQS and SNS respectively.

VM args -
java -Dlogsdir=<log_path> -jar <base_path>\intuit-profile-service\target\intuit-profile-service-1.0.0-SNAPSHOT.jar
