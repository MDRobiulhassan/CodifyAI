const http = require("http");

const server = http.createServer((req, res) => {
    const hostname = req.headers.host?.split(":")[0] || "";

    const match = hostname.match(/^project-(\d+)\.localhost$/);

    if (!match) {
        res.writeHead(400);
        return res.end("Use http://project-{id}.localhost:8091");
    }

    const projectId = match[1];

    const proxyReq = http.request(
        {
            hostname: "localhost",
            port: 8090,
            path: req.url,
            method: req.method,
            headers: {
                ...req.headers,
                host: `project-${projectId}.app.codify-ai.com`,
            },
        },
        (proxyRes) => {
            res.writeHead(proxyRes.statusCode, proxyRes.headers);
            proxyRes.pipe(res);
        }
    );

    proxyReq.on("error", (err) => {
        res.writeHead(502);
        res.end(err.message);
    });

    req.pipe(proxyReq);
});

server.on("upgrade", (req, socket, head) => {
    const hostname = req.headers.host?.split(":")[0] || "";
    const match = hostname.match(/^project-(\d+)\.localhost$/);

    if (!match) {
        socket.destroy();
        return;
    }

    const projectId = match[1];

    const proxyReq = http.request({
        hostname: "localhost",
        port: 8090,
        path: req.url,
        headers: {
            ...req.headers,
            host: `project-${projectId}.app.codify-ai.com`,
        },
    });

    proxyReq.on("upgrade", (res, upstreamSocket, upstreamHead) => {
        socket.write(
            `HTTP/1.1 101 Switching Protocols\r\n` +
            Object.entries(res.headers)
                .map(([key, value]) => `${key}: ${value}`)
                .join("\r\n") +
            "\r\n\r\n"
        );

        if (upstreamHead.length) socket.write(upstreamHead);
        if (head.length) upstreamSocket.write(head);

        socket.pipe(upstreamSocket);
        upstreamSocket.pipe(socket);
    });

    proxyReq.on("error", () => socket.destroy());
    proxyReq.end();
});

server.listen(8091, () => {
    console.log("Dynamic CodifyAI proxy running on port 8091");
});
