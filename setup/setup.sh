echo "Creating Gogs user..."
curl \
 --retry 20 \
 -f \
 --retry-all-errors \
 --retry-delay 30 \
 -d "user_name=openknowledge&email=test@openknowledge.de&password=workshop&retype=workshop" \
 -X POST http://gogs-service:3000/user/sign_up
echo "Gogs user created."

echo "Creating repositories..."

curl \
 -f \
 -u "openknowledge:workshop" \
 -d "name=customer-service" \
 -X POST http://gogs-service:3000/api/v1/admin/users/openknowledge/repos
echo "Repository 'customer-service' created."

curl \
 -f \
 -u "openknowledge:workshop" \
 -d "name=billing-service" \
 -X POST http://gogs-service:3000/api/v1/admin/users/openknowledge/repos
echo "Repository 'billing-service' created."

curl \
 -f \
 -u "openknowledge:workshop" \
 -d "name=delivery-service" \
 -X POST http://gogs-service:3000/api/v1/admin/users/openknowledge/repos
echo "Repository 'delivery-service' created."

curl \
 -f \
 -u "openknowledge:workshop" \
 -d "name=address-validation-service" \
 -X POST http://gogs-service:3000/api/v1/admin/users/openknowledge/repos
echo "Repository 'address-validation-service' created."
