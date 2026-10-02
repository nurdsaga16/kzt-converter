# KZT Converter

## What it does

A small HTTP service that converts amounts between Kazakhstani tenge (KZT)
and a few other currencies (USD, EUR, RUB). Rates are hard-coded — there is
no database and no external API.

| Endpoint | Example | Answer |
|---|---|---|
| `GET /` | `/` | short greeting |
| `GET /healthz` | `/healthz` | `ok` (200) |
| `GET /rates` | `/rates` | tenge per 1 unit of each currency |
| `GET /convert` | `/convert?from=USD&to=KZT&amount=10` | `5000.00` |

Unknown currency or a missing / non-numeric `amount` returns `400`.

## How to run it

Needs JDK 17+ (no Maven or Gradle).

```bash
./scripts/run.sh                 # listens on port 8080
PORT=9000 ./scripts/run.sh       # or any port you choose
curl "localhost:8080/convert?from=EUR&to=KZT&amount=3"
```

The service reads the port from the `PORT` environment variable, defaults to
**8080**, and binds to `0.0.0.0`.

## How to test it

```bash
./scripts/test.sh
```

Starts the real server on a free port, calls it over HTTP, prints one line per
check and a final `TESTS: n/n`. Exits `0` only when every test passes.

## Layout

```
src/Server.java        HTTP endpoints
src/Converter.java     conversion logic and rates
tests/ServerTests.java tests (plain Java, no JUnit)
scripts/run.sh         start the service
scripts/test.sh        run the tests
```
