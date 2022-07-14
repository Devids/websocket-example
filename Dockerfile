#https://www.alibabacloud.com/blog/how-to-build-nginx-from-source-on-ubuntu-20-04-lts_597793
#https://github.com/nginx/njs/blob/master/nginx/ngx_stream_js_module.c#L546
#https://github.com/nginx/njs/blob/master/nginx/ngx_stream_js_module.c#L88


FROM debian:bullseye-slim

RUN mkdir nginx
WORKDIR /nginx
ADD ./njs/ ./njs/
RUN apt-get update 
RUN apt-get install -y build-essential libpcre3 libpcre3-dev zlib1g zlib1g-dev libssl-dev libgd-dev libxml2 libxml2-dev uuid-dev wget
RUN wget http://nginx.org/download/nginx-1.23.0.tar.gz
RUN tar -zxvf nginx-1.23.0.tar.gz
WORKDIR /nginx/nginx-1.23.0
RUN ./configure --prefix=/var/www/html --sbin-path=/usr/sbin/nginx --conf-path=/etc/nginx/nginx.conf --http-log-path=/var/log/nginx/access.log --error-log-path=/var/log/nginx/error.log --with-pcre  --lock-path=/var/lock/nginx.lock --pid-path=/var/run/nginx.pid --with-http_ssl_module --modules-path=/etc/nginx/modules --with-http_v2_module --with-stream=dynamic --with-http_addition_module --with-http_mp4_module --add-dynamic-module=/nginx/njs/nginx
RUN make
RUN make install

#    while (njs_vm_pending(ctx->vm)) {
#        ngx_log_error(NGX_LOG_ERR, s->connection->log, 0,
#                      "DEBUGGGGGGGGGGG waiting..");
#        sleep(1);
#    }