const http = require("http");
const httpProxy = require("http-proxy");
const Redis = require("ioredis");

const redisUrl = process.env.REDIS_URL || "redis://redis-service:6379";

const redis = new Redis(redisUrl, {
  maxRetriesPerRequest: null,
  enableReadyCheck: false,
  retryStrategy: function (times) {
    const delay = Math.min(times * 50, 2000);
    console.log(`Redis connection lost. Retrying in ${delay}ms...`);
    return delay;
  },
});

redis.on("error", (err) => {
  console.error("Redis Client error:", err);
});

redis.on("connect", () => {
  console.log("Connected to Redis Successfully");
});

const proxy = httpProxy.createProxyServer({
  ws: true,
  xfwd: true,
  changeOrigin: true,
});

async function getTarget(hostname) {
  try {
    const targetIp = await redis.get(`route:${hostname}`);
    if (targetIp) {
      return targetIp;
    }
  } catch (error) {
    console.error("Error occurred while fetching target from Redis:", error);
  }
}

const getTargetUrl = (ip) => {
  return ip.includes(":") ? `http://[${ip}]` : `http://${ip}:5173`;
};

const server = http.createServer(async (req, res) => {
  const rawHost = req.headers.host || "";
  const hostName = rawHost.split(":")[0];

  const targetIp = await getTarget(hostName);

  if (!targetIp) {
    res.writeHead(404, { "Content-Type": "text/plain" });
    return res.end("Not Found");
  }

  const target = getTargetUrl(targetIp);
  console.log(`HTTP Proxy: ${hostName} -> ${target}${req.url}`);

  proxy.web(req, res, { target }, (err) => {
    console.error(`Proxy error (Web): ${hostName}`, err.message);
    if (!res.headersSent) {
      res.writeHead(502);
      res.end("Vite server unavailable...");
    }
  });
});

server.on("upgrade", async (req, socket, head) => {
  const rawHost = req.headers.host || "";
  const hostName = rawHost.split(":")[0];

  const targetIp = await getTarget(hostName);

  if (targetIp) {
    const target = getTargetUrl(targetIp);
    console.log(`WS Upgrade: ${hostName} -> ${target}${req.url}`);

    proxy.ws(req, socket, head, { target }, (err) => {
      console.error(`Proxy error (WebSocket): ${hostName}`, err.message);
      socket.destroy();
    });
  } else {
    socket.destroy();
  }
});

server.listen(80, () => {
  console.log("Proxy server listening on port 80");
});
