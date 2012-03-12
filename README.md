chaplib
=======
If HTTP is an application protocol, then what's the functionality supported
by this application? All the HTTP libraries we've seen before give you access
to headers, response codes, methods, etc., which is great--and necessary--but
those are *protocol* details that hide the *application* underneath.

chaplib is meant to provide that application-level interface, taking care of
the protocol details for you (and maintaining unconditional HTTP/1.1
compliance) and letting you work on getting your work done, while still
providing enough customization to get the protocol behavior you desire.

What's in a name?
-----------------
chap = *Client* using *HTTP* as an *Application* *Protocol*

lib = um, library?
