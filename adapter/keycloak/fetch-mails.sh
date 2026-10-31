curl http://localhost:3000/api/emails/render \
  -X POST \
  -H 'Accept: application/json' \
  -H 'Content-Type: application/json' \
  --data-raw '{"name":"onboarding.vue","pretty":true}' \
  > src/main/resources/mail/onboarding.html
