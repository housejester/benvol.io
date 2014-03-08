benvol.io
=========

> I do but keep the peace: put up thy sword, 

> Or manage it to part these men with me.

Benvolio is an authenticating query facade for ElasticSearch that provides fine-grained
control over which types of users can execute which kinds of requests against an
ElasticSearch cluster.

To configure Benvolio, you just create a collection of **policy declarations** defining
the different kinds of users in your system, and the different kinds of ElasticSearch
queries those users are allowed to invoke, and Benvolio protects your data against
unauthorized operations.

Benvolio stores its policy declarations as ElasticSearch types, indexed alongside your
own data, so it uses technology you've already invested in.

In many cases, Benvolio provides a complete backend for your service, obviating the need
to write your own REST API at all.

### Status

Benvolio is in the early stages of development. Check back later for more info :)