sed -i '1i \/\/ @ts-nocheck' "$1"
sed -i '/^[[:space:]]*\* The version of the OpenAPI document:/d' app/api/backend/generated/*/*.ts
