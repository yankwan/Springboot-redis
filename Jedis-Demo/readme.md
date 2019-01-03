#### jedis多线程并发错误

* jedis并发问题

测试用例`ConcurrentTest`呈现了并发状态下连接jedis导致出现连接异常的错误信息。

```
at redis.clients.jedis.Connection.connect(Connection.java:207)
at redis.clients.jedis.BinaryClient.connect(BinaryClient.java:93)
```

由堆栈信息查看对应源码，`Connection.java`可以发现，`connect()`方法不是线程安全的，当共享jedis实例时候会出现并发问题。

`connect()`源码如下：

```java
public void connect() {
    if (!this.isConnected()) {
        try {
            this.socket = new Socket();
            this.socket.setReuseAddress(true);
            this.socket.setKeepAlive(true);
            this.socket.setTcpNoDelay(true);
            this.socket.setSoLinger(true, 0);
            this.socket.connect(new InetSocketAddress(this.host, this.port), this.connectionTimeout);
            this.socket.setSoTimeout(this.soTimeout);
            if (this.ssl) {
                if (null == this.sslSocketFactory) {
                    this.sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
                }

                this.socket = (SSLSocket)this.sslSocketFactory.createSocket(this.socket, this.host, this.port, true);
                if (null != this.sslParameters) {
                    ((SSLSocket)this.socket).setSSLParameters(this.sslParameters);
                }

                if (null != this.hostnameVerifier && !this.hostnameVerifier.verify(this.host, ((SSLSocket)this.socket).getSession())) {
                    String message = String.format("The connection to '%s' failed ssl/tls hostname verification.", this.host);
                    throw new JedisConnectionException(message);
                }
            }

            this.outputStream = new RedisOutputStream(this.socket.getOutputStream());
            this.inputStream = new RedisInputStream(this.socket.getInputStream());
        } catch (IOException var2) {
            this.broken = true;
            throw new JedisConnectionException(var2);
        }
    }

}
```

当线程1执行到下面这两句时：

```java
this.outputStream = new RedisOutputStream(this.socket.getOutputStream());
this.inputStream = new RedisInputStream(this.socket.getInputStream());
```

如果此时线程2执行到下面两句之间，即将socket重新初始化但并未获取连接的时候：

```java
this.socket = new Socket();
/*** 线程2执行到此处 ***/
this.socket.connect(new InetSocketAddress(this.host, this.port), this.connectionTimeout);
```

此时线程1通过`this.socket.getOutputStream()`就会抛出连接异常信息。


* 使用jedis pool解决并发问题

使用jedis提供的JedisPool连接池处理并发问题。参考`ConcurrentPoolTest`测试用用例。
需要注意的是，每个线程中必须使用单独的一个jedis实例，如果多个线程共享同个jedis实例仍然会出现并发文天。