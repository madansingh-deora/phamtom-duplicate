URL="http://localhost:8080/submit"
PAYLOAD_FILE="payload.json"
CONCURRENCY=20

for i in $(seq 1 $CONCURRENCY); do
  (
    # use curl; -s silent, -w to print HTTP code, -o to show body
    curl -s -X POST "$URL" \
      -H "Content-Type: application/json" \
      --data-binary @"$PAYLOAD_FILE" \
      -w "\n--done-- request #%d HTTP_CODE:%{http_code}\n" \
      || echo "curl failed for request $i"
  ) &
done

wait
echo "All requests completed."
