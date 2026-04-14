# UDP

This is a simple Java project showing basic UDP communication between processes.

The program works in two modes:
- the first instance becomes the main process
- the next instances work as clients and send numbers to it

The main process collects numbers sent by clients.

## How it works

Each program is started with two arguments:

```bash
java DAS <port> <number>
