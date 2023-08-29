echo "Creating Gogs user..."
curl \
 --retry 20 \
 -f \
 --retry-all-errors \
 --retry-delay 30 \
 -o /dev/null \
 -d "user_name=openknowledge&email=test@openknowledge.de&password=workshop&retype=workshop" \
 -X POST http://gogs-service:3000/user/sign_up
echo "Gogs user created."
