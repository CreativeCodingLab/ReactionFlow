         
/* https://github.com/d3/d3-xhr Copyright 2015 Mike Bostock */
"undefined"==typeof Map?(Map=function(){this.clear()},Map.prototype={set:function(e,n){return this._[e]=n,this},get:function(e){return this._[e]},has:function(e){return e in this._},"delete":function(e){return e in this._&&delete this._[e]},clear:function(){this._=Object.create(null)},get size(){var e=0;for(var n in this._)++e;return e},forEach:function(e){for(var n in this._)e(this._[n],n,this)}}):function(){var e=new Map;e.set(0,0)!==e&&(e=e.set,Map.prototype.set=function(){return e.apply(this,arguments),this})}(),function(e,n){"object"==typeof exports&&"undefined"!=typeof module?n(exports):"function"==typeof define&&define.amd?define(["exports"],n):n(e.xhr={})}(this,function(e){"use strict";function n(e){function n(e,n){var r;return t(e,function(e,t){if(r)return r(e,t-1);var o=new Function("d","return {"+e.map(function(e,n){return JSON.stringify(e)+": d["+n+"]"}).join(",")+"}");r=n?function(e,t){return n(o(e),t)}:o})}function t(e,n){function t(){if(f>=c)return i;if(o)return o=!1,u;var n=f;if(34===e.charCodeAt(n)){for(var t=n;t++<c;)if(34===e.charCodeAt(t)){if(34!==e.charCodeAt(t+1))break;++t}f=t+2;var r=e.charCodeAt(t+1);return 13===r?(o=!0,10===e.charCodeAt(t+2)&&++f):10===r&&(o=!0),e.slice(n+1,t).replace(/""/g,'"')}for(;c>f;){var r=e.charCodeAt(f++),a=1;if(10===r)o=!0;else if(13===r)o=!0,10===e.charCodeAt(f)&&(++f,++a);else if(r!==s)continue;return e.slice(n,f-a)}return e.slice(n)}for(var r,o,u={},i={},a=[],c=e.length,f=0,l=0;(r=t())!==i;){for(var p=[];r!==u&&r!==i;)p.push(r),r=t();n&&null==(p=n(p,l++))||a.push(p)}return a}function r(n){if(Array.isArray(n[0]))return o(n);var t=Object.create(null),r=[];return n.forEach(function(e){for(var n in e)(n+="")in t||r.push(t[n]=n)}),[r.map(i).join(e)].concat(n.map(function(n){return r.map(function(e){return i(n[e])}).join(e)})).join("\n")}function o(e){return e.map(u).join("\n")}function u(n){return n.map(i).join(e)}function i(e){return a.test(e)?'"'+e.replace(/\"/g,'""')+'"':e}var a=new RegExp('["'+e+"\n]"),s=e.charCodeAt(0);return{parse:n,parseRows:t,format:r,formatRows:o}}function t(e,n){return function(t){return e.parse(t.responseText,n)}}function r(e){return function(n,t){e(null==n?t:null)}}function o(e){var n=e.responseType;return n&&"text"!==n?e.response:e.responseText}function u(e){function n(e){var n=(e+="").indexOf("."),t=e;if(n>=0?e=e.slice(0,n):t+=".",e&&!i.hasOwnProperty(e))throw new Error("unknown type: "+e);return{type:e,name:t}}function t(e){return function(){for(var n,t,r=i[e],o=-1,u=r.length;++o<u;)(t=(n=r[o]).value)&&t.apply(this,arguments);return s}}var r,o=-1,u=e.length,i={},a={},s=this;for(s.on=function(e,t){if(e=n(e),arguments.length<2)return(t=a[e.name])&&t.value;if(e.type){var r,o=i[e.type],u=a[e.name];u&&(u.value=null,r=o.indexOf(u),i[e.type]=o=o.slice(0,r).concat(o.slice(r+1)),delete a[e.name]),t&&(t={value:t},a[e.name]=t,o.push(t))}else if(null==t)for(var c in i)if(t=a[c+e.name]){t.value=null;var o=i[c],r=o.indexOf(t);i[c]=o.slice(0,r).concat(o.slice(r+1)),delete a[t.name]}return s};++o<u;){if(r=e[o]+"",!r||r in s)throw new Error("illegal or duplicate type: "+r);i[r]=[],s[r]=t(r)}}function i(){return new u(arguments)}function a(e,n){function t(){var e,n=p.status;if(!n&&o(p)||n>=200&&300>n||304===n){if(s)try{e=s.call(u,p)}catch(t){return void f.error.call(u,t)}else e=p;f.load.call(u,e)}else f.error.call(u,p)}var u,a,s,c,f=i("beforesend","progress","load","error"),l=new Map,p=new XMLHttpRequest;return"undefined"==typeof XDomainRequest||"withCredentials"in p||!/^(http(s)?:)?\/\//.test(e)||(p=new XDomainRequest),"onload"in p?p.onload=p.onerror=t:p.onreadystatechange=function(){p.readyState>3&&t()},p.onprogress=function(e){f.progress.call(u,e)},u={header:function(e,n){return e=(e+"").toLowerCase(),arguments.length<2?l.get(e):(null==n?l["delete"](e):l.set(e,n+""),u)},mimeType:function(e){return arguments.length?(a=null==e?null:e+"",u):a},responseType:function(e){return arguments.length?(c=e,u):c},response:function(e){return s=e,u},get:function(e,n){return u.send("GET",e,n)},post:function(e,n){return u.send("POST",e,n)},send:function(n,t,o){return o||"function"!=typeof t||(o=t,t=null),o&&1===o.length&&(o=r(o)),p.open(n,e,!0),null==a||l.has("accept")||l.set("accept",a+",*/*"),p.setRequestHeader&&l.forEach(function(e,n){p.setRequestHeader(n,e)}),null!=a&&p.overrideMimeType&&p.overrideMimeType(a),null!=c&&(p.responseType=c),o&&u.on("error",o).on("load",function(e){o(null,e)}),f.beforesend.call(u,p),p.send(null==t?null:t),u},abort:function(){return p.abort(),u},on:function(){var e=f.on.apply(f,arguments);return e===f?u:e}},n?u.get(n):u}function s(e,n){return function(r,o,u){arguments.length<3&&(u=o,o=null);var i=a(r).mimeType(e);return i.row=function(e){return arguments.length?i.response(t(n,o=e)):o},i.row(o),u?i.get(u):i}}function c(e,n){return function(t,r){var o=a(t).mimeType(e).response(n);return r?o.get(r):o}}var f=n(" ");i.prototype=u.prototype;var l=s("text/tab-separated-values",f),p=n(","),h=s("text/csv",p),d=c("application/xml",function(e){return e.responseXML}),m=c("text/plain",function(e){return e.responseText}),v=c("application/json",function(e){return JSON.parse(e.responseText)}),g=c("text/html",function(e){return document.createRange().createContextualFragment(e.responseText)});e.xhr=a,e.html=g,e.json=v,e.text=m,e.xml=d,e.csv=h,e.tsv=l});

/* https://github.com/d3/d3-hierarchy Copyright 2015 Mike Bostock */
!function(n,r){"object"==typeof exports&&"undefined"!=typeof module?r(exports):"function"==typeof define&&define.amd?define(["exports"],r):r(n.hierarchy={})}(this,function(n){"use strict";function r(n){var r=[];return n.forEach(function(n){n.children&&n.children.forEach(function(e){r.push({source:n,target:e})})}),r}function e(n,e){return n.sort=function(){var r=e.sort.apply(e,arguments);return r===e?n:r},n.children=function(){var r=e.children.apply(e,arguments);return r===e?n:r},n.value=function(){var r=e.value.apply(e,arguments);return r===e?n:r},n.nodes=n,n.links=r,n}function t(n){return{x:n.x,y:n.y,dx:n.dx,dy:n.dy}}function u(n,r){var e=n.x+r[3],t=n.y+r[0],u=n.dx-r[1]-r[3],a=n.dy-r[0]-r[2];return 0>u&&(e+=u/2,u=0),0>a&&(t+=a/2,a=0),{x:e,y:t,dx:u,dy:a}}function a(n,r){for(var e=[n],t=[];null!=(n=e.pop());)if(t.push(n),(a=n.children)&&(u=a.length))for(var u,a,l=-1;++l<u;)e.push(a[l]);for(;null!=(n=t.pop());)r(n)}function l(n,r){for(var e=[n];null!=(n=e.pop());)if(r(n),(u=n.children)&&(t=u.length))for(var t,u;--t>=0;)e.push(u[t])}function i(n){return n.value}function c(n){return n.children}function o(n,r){return r.value-n.value}function f(){function n(r){var l,i=[r],c=[];for(r.parent=null,r.depth=0;null!=(l=i.pop());)if(c.push(l),(f=t.call(n,l,l.depth))&&(o=f.length)){for(var o,f,h;--o>=0;)i.push(h=f[o]),h.parent=l,h.depth=l.depth+1;u&&(l.value=0),l.children=f}else u&&(l.value=+u.call(n,l,l.depth)||0),delete l.children;return a(r,function(n){var r,t;e&&(r=n.children)&&r.sort(e),u&&(t=n.parent)&&(t.value+=n.value)}),c}var e=o,t=c,u=i;return n.nodes=n,n.links=r,n.sort=function(r){return arguments.length?(e=r,n):e},n.children=function(r){return arguments.length?(t=r,n):t},n.value=function(r){return arguments.length?(u=r,n):u},n.revalue=function(r){return u&&(l(r,function(n){n.children&&(n.value=0)}),a(r,function(r){var e;r.children||(r.value=+u.call(n,r,r.depth)||0),(e=r.parent)&&(e.value+=r.value)})),r},n}function h(){function n(n){var r=x.call(h,n,n.depth);return null==r?t(n):u(n,"number"==typeof r?[r,r,r,r]:r)}function r(n){return u(n,x)}function a(n,r){for(var e,t,u=-1,a=n.length;++u<a;)t=(e=n[u]).value*(0>r?0:r),e.area=isNaN(t)||0>=t?0:t}function l(n){var r=n.children;if(r&&r.length){var e,t,u,i=y(n),f=[],h=r.slice(),d=1/0,p="slice"===m?i.dx:"dice"===m?i.dy:"slice-dice"===m?1&n.depth?i.dy:i.dx:Math.min(i.dx,i.dy);for(a(h,i.dx*i.dy/n.value),f.area=0;(u=h.length)>0;)f.push(e=h[u-1]),f.area+=e.area,"squarify"!==m||(t=c(f,p))<=d?(h.pop(),d=t):(f.area-=f.pop().area,o(f,p,i,!1),p=Math.min(i.dx,i.dy),f.length=f.area=0,d=1/0);f.length&&(o(f,p,i,!0),f.length=f.area=0),r.forEach(l)}}function i(n){var r=n.children;if(r&&r.length){var e,t=y(n),u=r.slice(),l=[];for(a(u,t.dx*t.dy/n.value),l.area=0;e=u.pop();)l.push(e),l.area+=e.area,null!=e.z&&(o(l,e.z?t.dx:t.dy,t,!u.length),l.length=l.area=0);r.forEach(i)}}function c(n,r){for(var e,t=n.area,u=0,a=1/0,l=-1,i=n.length;++l<i;)(e=n[l].area)&&(a>e&&(a=e),e>u&&(u=e));return t*=t,r*=r,t?Math.max(r*u*_/t,t/(r*a*_)):1/0}function o(n,r,e,t){var u,a=-1,l=n.length,i=e.x,c=e.y,o=r?v(n.area/r):0;if(r==e.dx){for((t||o>e.dy)&&(o=e.dy);++a<l;)u=n[a],u.x=i,u.y=c,u.dy=o,i+=u.dx=Math.min(e.x+e.dx-i,o?v(u.area/o):0);u.z=!0,u.dx+=e.x+e.dx-i,e.y+=o,e.dy-=o}else{for((t||o>e.dx)&&(o=e.dx);++a<l;)u=n[a],u.x=i,u.y=c,u.dx=o,c+=u.dy=Math.min(e.y+e.dy-c,o?v(u.area/o):0);u.z=!1,u.dy+=e.y+e.dy-c,e.x+=o,e.dx-=o}}function h(n){var r=d||p(n),e=r[0];return e.x=0,e.y=0,e.dx=s[0],e.dy=s[1],d&&p.revalue(e),a([e],e.dx*e.dy/e.value),(d?i:l)(e),g&&(d=r),r}var d,p=f(),v=Number,s=[1,1],x=null,y=t,g=!1,m="squarify",_=V;return h.size=function(n){return arguments.length?(s=[+n[0],+n[1]],h):s.slice()},h.padding=function(e){if(!arguments.length)return Array.isArray(x)?x.slice():x;var u;return y=null==e?(x=null,t):"function"==(u=typeof e)?(x=e,n):"number"===u?(x=[e,e,e,e],r):(x=[+e[0],+e[1],+e[2],+e[3]],r),h},h.round=function(n){return arguments.length?(v=n?Math.round:Number,h):v!==Number},h.sticky=function(n){return arguments.length?(g=!!n,d=null,h):g},h.ratio=function(n){return arguments.length?(_=+n,h):_},h.mode=function(n){return arguments.length?(m=U.hasOwnProperty(n)?n+"":"squarify",h):m},e(h,p)}function d(n){var r=n.children;return r.length?r[0]:n.t}function p(n){var r,e=n.children;return(r=e.length)?e[r-1]:n.t}function v(n,r,e){return n.a.parent===r.parent?n.a:e}function s(n,r,e){var t=e/(r.i-n.i);r.c-=t,r.s+=e,n.c+=t,r.z+=e,r.m+=e}function x(n){for(var r,e=0,t=0,u=n.children,a=u.length;--a>=0;)r=u[a],r.z+=e,r.m+=e,e+=r.s+(t+=r.c)}function y(n,r){return n.parent===r.parent?1:2}function g(){function n(n,e){var i=o.call(this,n,e),f=i[0],d=r(f);if(a(d,t),d.parent.m=-d.z,l(d,u),m)l(f,c);else{var p=f,v=f,s=f;l(f,function(n){n.x<p.x&&(p=n),n.x>v.x&&(v=n),n.depth>s.depth&&(s=n)});var x=h(p,v)/2-p.x,y=g[0]/(v.x+h(v,p)/2+x),_=g[1]/(s.depth||1);l(f,function(n){n.x=(n.x+x)*y,n.y=n.depth*_})}return i}function r(n){for(var r,e={A:null,children:[n]},t=[e];null!=(r=t.pop());)for(var u,a=r.children,l=0,i=a.length;i>l;++l)t.push((a[l]=u={_:a[l],parent:r,children:(u=a[l].children)&&u.slice()||[],A:null,a:null,z:0,m:0,c:0,s:0,t:null,i:l}).a=u);return e.children[0]}function t(n){var r=n.children,e=n.parent.children,t=n.i?e[n.i-1]:null;if(r.length){x(n);var u=(r[0].z+r[r.length-1].z)/2;t?(n.z=t.z+h(n._,t._),n.m=n.z-u):n.z=u}else t&&(n.z=t.z+h(n._,t._));n.parent.A=i(n,t,n.parent.A||e[0])}function u(n){n._.x=n.z+n.parent.m,n.m+=n.parent.m}function i(n,r,e){if(r){for(var t,u=n,a=n,l=r,i=u.parent.children[0],c=u.m,o=a.m,f=l.m,x=i.m;l=p(l),u=d(u),l&&u;)i=d(i),a=p(a),a.a=n,t=l.z+f-u.z-c+h(l._,u._),t>0&&(s(v(l,n,e),n,t),c+=t,o+=t),f+=l.m,c+=u.m,x+=i.m,o+=a.m;l&&!p(a)&&(a.t=l,a.m+=f-o),u&&!d(i)&&(i.t=u,i.m+=c-x,e=n)}return e}function c(n){n.x*=g[0],n.y=n.depth*g[1]}var o=f().sort(null).value(null),h=y,g=[1,1],m=null;return n.separation=function(r){return arguments.length?(h=r,n):h},n.size=function(r){return arguments.length?(m=null==(g=r)?c:null,n):m?null:g},n.nodeSize=function(r){return arguments.length?(m=null==(g=r)?null:c,n):m?g:null},e(n,o)}function m(n){var r,e,t=n.children,u=0;if(t)for(r=0,e=t.length;e>r;++r)u=Math.max(u,m(t[r]));return 1+u}function _(n,r,e,t){var u=n.children;if(n.x=r,n.y=n.depth*t,n.dx=e,n.dy=t,u&&(a=u.length)){var a,l,i,c=-1;for(e=n.value?e/n.value:0;++c<a;)_(l=u[c],r,i=l.value*e,t),r+=i}}function k(){function n(n,e){var u=r.call(this,n,e);return _(u[0],0,t[0],t[1]/m(u[0])),u}var r=f(),t=[1,1];return n.size=function(r){return arguments.length?(t=[+r[0],+r[1]],n):t.slice()},e(n,r)}function z(n,r,e,t){var u=n.children;if(n.x=r+=t*n.x,n.y=e+=t*n.y,n.r*=t,u)for(var a=-1,l=u.length;++a<l;)z(u[a],r,e,t)}function M(n){delete n._pack_next,delete n._pack_prev}function b(n){for(var r,e=(n=n.slice()).length,t=null,u=t;e;){var a={id:n.length-e,value:n[e-1],next:null};u=u?u.next=a:t=a,n[r]=n[--e]}return{head:t,tail:u}}function q(n,r){var e=n.x-r.x,t=n.y-r.y;return Math.sqrt(e*e+t*t)<n.r-r.r+1e-6}function A(n,r,e){var t=n.x,u=n.y,a=n.r,l=r.x,i=r.y,c=r.r,o=e.x,f=e.y,h=e.r,d=2*(t-l),p=2*(u-i),v=2*(c-a),s=t*t+u*u-a*a-l*l-i*i+c*c,x=2*(t-o),y=2*(u-f),g=2*(h-a),m=t*t+u*u-a*a-o*o-f*f+h*h,_=x*p-d*y,k=(p*m-y*s)/_-t,z=(y*v-p*g)/_,M=(x*s-d*m)/_-u,b=(d*g-x*v)/_,q=z*z+b*b-1,A=2*(k*z+M*b+a),E=k*k+M*M-a*a,N=(-A-Math.sqrt(A*A-4*q*E))/(2*q);return{x:k+z*N+t,y:M+b*N+u,r:N}}function E(n,r){var e=n.x,t=n.y,u=n.r,a=r.x,l=r.y,i=r.r,c=a-e,o=l-t,f=i-u,h=Math.sqrt(c*c+o*o);return{x:(e+a+c/h*f)/2,y:(t+l+o/h*f)/2,r:(h+u+i)/2}}function N(n,r){var e,t,u,a=null,l=n.head;switch(r.length){case 1:e=r[0];break;case 2:e=E(r[0],r[1]);break;case 3:e=A(r[0],r[1],r[2])}for(;l;)u=l.value,t=l.next,e&&q(e,u)?a=l:(a?(n.tail=a,a.next=null):n.head=n.tail=null,r.push(u),e=N(n,r),r.pop(),n.head?(l.next=n.head,n.head=l):(l.next=null,n.head=n.tail=l),a=n.tail,a.next=t),l=t;return n.tail=a,e}function w(n){return N(b(n),[])}function S(n,r){var e=n._pack_next;n._pack_next=r,r._pack_prev=n,r._pack_next=e,e._pack_prev=r}function j(n,r){n._pack_next=r,r._pack_prev=n}function O(n,r){var e=r.x-n.x,t=r.y-n.y,u=n.r+r.r;return.999*u*u>e*e+t*t}function P(n,r,e){var t=n.r+e.r,u=r.x-n.x,a=r.y-n.y;if(t&&(u||a)){var l=r.r+e.r,i=u*u+a*a;l*=l,t*=t;var c=.5+(t-l)/(2*i),o=Math.sqrt(Math.max(0,2*l*(t+i)-(t-=i)*t-l*l))/(2*i);e.x=n.x+c*u+o*a,e.y=n.y+c*a-o*u}else e.x=n.x+t,e.y=n.y}function B(n){n._pack_next=n._pack_prev=n}function C(n){if((r=n.children)&&(c=r.length)){var r,e,t,u,a,l,i,c;if(r.forEach(B),e=r[0],e.x=-e.r,e.y=0,c>1&&(t=r[1],t.x=t.r,t.y=0,c>2))for(u=r[2],P(e,t,u),S(e,u),e._pack_prev=u,S(u,t),t=e._pack_next,a=3;c>a;a++){P(e,t,u=r[a]);var o=0,f=1,h=1;for(l=t._pack_next;l!==t;l=l._pack_next,f++)if(O(l,u)){o=1;break}if(1==o)for(i=e._pack_prev;i!==l._pack_prev&&!O(i,u);i=i._pack_prev,h++);o?(h>f||f==h&&t.r<e.r?j(e,t=l):j(e=i,t),a--):(S(e,u),t=u)}var u=w(r);for(a=0;c>a;++a)e=r[a],e.x-=u.x,e.y-=u.y;n.r=u.r,r.forEach(M)}}function D(n,r){return n.value-r.value}function F(){function n(n,e){var i=t.call(this,n,e),c=i[0],o=l[0],f=l[1],h=null==r?Math.sqrt:"function"==typeof r?r:function(){return r};if(c.x=c.y=0,a(c,function(n){n.r=+h(n.value)}),a(c,C),u){var d=u*(r?1:Math.max(2*c.r/o,2*c.r/f))/2;a(c,function(n){n.r+=d}),a(c,C),a(c,function(n){n.r-=d})}return z(c,o/2,f/2,r?1:1/Math.max(2*c.r/o,2*c.r/f)),i}var r,t=f().sort(D),u=0,l=[1,1];return n.size=function(r){return arguments.length?(l=[+r[0],+r[1]],n):l.slice()},n.radius=function(e){return arguments.length?(r=null==e||"function"==typeof e?e:+e,n):r},n.padding=function(r){return arguments.length?(u=+r,n):u},e(n,t)}function G(n){for(var r,e;(r=n.children)&&(e=r.length);)n=r[e-1];return n}function H(n){for(var r;(r=n.children)&&r.length;)n=r[0];return n}function I(n){return 1+n.reduce(function(n,r){return Math.max(n,r.y)},0)}function J(n){return n.reduce(function(n,r){return n+r.x},0)/n.length}function K(){function n(n,e){var i,c=r.call(this,n,e),o=c[0],f=0;a(o,function(n){var r=n.children;r&&r.length?(n.x=J(r),n.y=I(r)):(n.x=i?f+=t(n,i):0,n.y=0,i=n)});var h=H(o),d=G(o),p=h.x-t(h,d)/2,v=d.x+t(d,h)/2;return a(o,l?function(n){n.x=(n.x-o.x)*u[0],n.y=(o.y-n.y)*u[1]}:function(n){n.x=(n.x-p)/(v-p)*u[0],n.y=(1-(o.y?n.y/o.y:1))*u[1]}),c}var r=f().sort(null).value(null),t=y,u=[1,1],l=!1;return n.separation=function(r){return arguments.length?(t=r,n):t},n.size=function(r){return arguments.length?(l=null==(u=r),n):l?null:u},n.nodeSize=function(r){return arguments.length?(l=null!=(u=r),n):l?u:null},e(n,r)}function L(n){for(var r=[],e=n.parent;null!=e;)r.push(n),n=e,e=e.parent;return r.push(n),r}function Q(n,r){if(n===r)return n;var e=L(n),t=L(r),u=null;for(n=e.pop(),r=t.pop();n===r;)u=n,n=e.pop(),r=t.pop();return u}function R(n,r){for(var e=Q(n,r),t=[n];n!==e;)n=n.parent,t.push(n);for(var u=t.length;r!==e;)t.splice(u,0,r),r=r.parent;return t}function T(){return function(n){for(var r,e=-1,t=n.length,u=new Array(t);++e<t;)r=n[e],u[e]=R(r.source,r.end);return u}}var U={slice:1,dice:1,"slice-dice":1,squarify:1},V=(1+Math.sqrt(5))/2;n.bundle=T,n.cluster=K,n.links=r,n.pack=F,n.partition=k,n.tree=g,n.treemap=h});

//Append a SVG to the body of the html page. Assign this SVG as an object to svg
var width = 1000;
var height = 1000;

var svg;// = //d3.select("body").append("svg")
    //.attr("height", height);

//Set up the force layout
/*var force = d3.layout.force()
    .charge(-100)
   // .linkStrength(10)
    .linkDistance(50)
    .gravity(0.05)
    //.friction(0.5)
    .alpha(0.1)
    .size([width, height]);*/
 //var force = cola.d3adaptor()
 //   .linkDistance(50)
 //   .size([width, height]);   

var cola = cola.d3adaptor()
        .linkDistance(40)
       // .avoidOverlaps(true)
       // .handleDisconnected(false)
        .size([width, height]);


var nodes, links;
var link, node, nodeText;
var nodeRadius = 13;


var root;

var groups2, nodes2, links2;
        
var times = 0;       
var maxDepth=1;         
   var complexes;
function vis() {
    
    nodes = [];    
    ["complex", "protein"].forEach(function(type) {
        ["left", "right"].forEach(function(side) {
            chart.data().participants[side][type].forEach(function(d) {
                if (isContainedChild(nodes, d)<0){
                    d.type = type;
                    d.name = d3.select(d.node).select("displayName").text();
                    d.id = d3.select(d.node).attr("rdf:ID");
                    nodes.push(d);
                }                   
            });    
        });
    });
    chart.data().reactions.forEach(function(d) {
        if (isContainedChild(nodes, d)<0){
            d.name = d3.select(d.node).select("displayName").text();
            d.id = d3.select(d.node).attr("rdf:ID");
            nodes.push(d);
        }
            
    });  

    groups2 = [];  
    nodes2 =[]; 
    // Read all pathway information and their direct children 
    chart.data().pathways.forEach(function(d) {
        var node1 = {}; 
        node1.id = d3.select(d.node).attr("rdf:ID");
        node1.name = d3.select(d.node).select("displayName").text();
        node1.node = d.node;
        node1.deep = 1;
        groups2.push(node1);
        
        
        var allCom = d3.select(d.node).selectAll("pathwayComponent")[0];
        allCom.forEach(function(com){
            var node2 ={};
            node2.id = d3.select(com).attr("rdf:resource").substring(1);
            if (node2.id.indexOf("Pathway") > -1){
                if (!node1.groupNames)
                    node1.groupNames = [];
                node1.groupNames.push(node2); 
            }
            else{
                nodes2.push(node2); // for the node arrayList
                if (!node1.leaves)
                    node1.leaves = [];
                node1.leaves.push(nodes2.length-1); // index of node in nodes2 list    
            }
        }); 
    }); 

    groups2.splice(0,1);// remove the root pathway

    // Connect parent-children node
    groups2.forEach(function(d) {
        if (d.groupNames){
            d.groups = [];
            d.groupNames.forEach(function(d2, c){
                var pathwayIndex;
                groups2.forEach(function(d4, i) {
                    var curId = d2.id;
                    var id4 = d4.id;
                    if (curId == id4){
                        pathwayIndex = i;
                    }    
                }); 
                if (pathwayIndex>=0){
                    d.groups.push(pathwayIndex);
                   // d.groups[c].isSubPathway = 1;
                }
            });  
        }   
    }); 
    // Set deep for groups
    for (var i=0;i<groups2.length;i++){
        setDeep(groups2[i])    
    }   
    function setDeep(gr){
         if (gr.groups){
            for (var j=0;j<gr.groups.length;j++){
                var index2 = gr.groups[j];
                groups2[index2].deep = gr.deep+1;
                if (gr.deep+1>maxDepth)
                    maxDepth = gr.deep+1;
                setDeep(groups2[index2])
            } 
        }    
    }    


    nodes.forEach(function(d) {
        if (d.id.indexOf("BiochemicalReaction") <0){
            //console.log(d.id);
            var node ={};
            node.id = d.id;
            node.name = d.name;
            node.node = d.node;
            node.deep = 1;
            node.index = nodes2.length;
            nodes2.push(node);
        }    
    });     




    links2 = [];
    
    var finish = [];
    chart.data().links.participantReaction.forEach(function(l) {
        var id1= d3.select(l.source.node).attr("rdf:ID");  // Participants
        var id2= d3.select(l.target.node).attr("rdf:ID");  // Reactions      
      // console.log("side="+l.source.side+" type="+l.source.type+"    name="+name1);
        var node1 =  getNodeById2(nodes2,id1);
        var node2 =  getNodeById2(nodes2,id2);     
        var participantSide =  l.source.side;

        if (node1 && node2){
            var newLink = {};
            if (participantSide=="left"){
                newLink.source = node1;
                newLink.target = node2;
                var groupId = getGroupById2(groups2, node2.id);  // add protein/complex to groups2
                groups2[groupId].leaves.push(node1.index);
                if (!node1.groups)
                    node1.groups =[];
                if (getIndex(node1.groups,groupId)<0)
                    node1.groups.push(groupId);
                if (getIndex(finish,node1.index)<0)
                    finish.push(node1.index);
                
             }
            else if (participantSide=="right"){
                newLink.source = node2;
                newLink.target = node1;
                var groupId = getGroupById2(groups2, node2.id); // add protein/complex to groups2
                groups2[groupId].leaves.push(node1.index);
                if (!node1.groups)
                    node1.groups =[];
                if (getIndex(node1.groups,groupId)<0)
                    node1.groups.push(groupId);
                if (getIndex(finish,node1.index)<0)
                    finish.push(node1.index);
                
            }
            else{
                throw new Error("Something went wrong: Can NOT get side of participant in a reaction");
            }    
            links2.push(newLink);
        }
    });  

    function getIndex(a, id2) {
        if (a){
            for (var i=0; i<a.length;i++){
                var id1 = a[i];
                if (id1==id2)
                    return i;
            }
        }
        return -1;
    }


    // Remove proteins/complexes belongs to multple groups
    for (var i=0; i<finish.length;i++){
        var nodeIndex = finish[i];
        var n = nodes2[nodeIndex];
        if (n.groups.length>1){
           // console.log(node.name+"   node.groups="+node.groups.length);
            for (var g=0; g<n.groups.length;g++){
                var groupId = n.groups[g];
                var list = groups2[groupId].leaves;
                for (var j=0; j<list.length;j++){
                    var nodeIndex2= list[j];
                    if (nodeIndex2==nodeIndex){
                        list.splice(j,1);
                        j--;
                    }
                } 
            }    
   
        }
    }    
    

    // Process links ****************************************************************
    /*
    links = [];    
    chart.data().links.participantReaction.forEach(function(l) {
        var name1= d3.select(l.source.node).select("displayName");  // Participants
        var name2= d3.select(l.target.node).select("displayName");  // Reactions      
      // console.log("side="+l.source.side+" type="+l.source.type+"    name="+name1);
        var node1 =  getNodeByName(nodes,name1);
        var node2 =  getNodeByName(nodes,name2);
        
        var participantSide =  l.source.side;

        if (node1 && node2){
            var newLink = {};
            if (participantSide=="left"){
                newLink.source = node1;
                newLink.target = node2;
            }
            else if (participantSide=="right"){
                newLink.source = node2;
                newLink.target = node1;
            }
            else{
                throw new Error("Something went wrong: Can NOT get side of participant in a reaction");
            }    
            links.push(newLink);
        }
    });  
   
    nodes.forEach(function (v) { v.width = v.height = nodeRadius/5; }); 
    force.nodes(nodes)
        .links(links)
        .flowLayout("y", 40)
       // .flowLayout("x", 30)
     //  .constraints(myconstraints)
        .symmetricDiffLinkLengths(30)
        .avoidOverlaps(true)
        .start();

    
    force.on("tick", update);    
     
       // define arrow markers for graph links
    

    link = svg.selectAll(".link")
            .data(links)
          .enter().append('svg:path')
            .style("stroke", "#000")
            .attr('class', 'link');
    

    node = svg.selectAll(".node")
      .data(nodes)
    .enter().append("circle")
      .attr("class", "node")
      .attr("r", 5)
      .style("fill", function(d) { return getColor(d.type); })
      .call(force.drag);


    nodeText = svg.selectAll(".nodeText")
      .data(nodes)
    .enter().append("text")
      .attr("class", "nodeText")
      .attr("dx", "5px")
      .style("fill", function(d) { return getColor(d.type); })
      .attr("font-family", "sans-serif")
        .attr("font-size", "10px")
        .style("text-anchor", "left")
       .text(function(d) { return d.name; });

    
       drawColorLegend();*/



    nodes2.forEach(function (v) { 
        v.width = v.height = nodeRadius; }); 

    var color = d3.scale.category20();

    svg = d3.select("body").append("svg")
        .attr("width", width)
        .attr("height", height);

    svg.append('svg:defs').append('svg:marker')
        .attr('id', 'end-arrow')
        .attr('viewBox', '0 -5 10 10')
        .attr('refX', 8)
        .attr('markerWidth', 5)
        .attr('markerHeight', 5)
        .attr('orient', 'auto')
      .append('svg:path')
        .attr('d', 'M0,-5L10,0L0,5')
        .attr('fill', '#000');

    var g;    
    cola    ///22222222222222
        .nodes(nodes2)
        .links(links2)
        .flowLayout("y", 30)
        .symmetricDiffLinkLengths(10)
        .avoidOverlaps(true)
        .start();
   
        
    var group2 = svg.selectAll(".group2")
        .data(groups2)
      .enter().append("rect")
        .attr("rx", 5).attr("ry", 5)
        .attr("class", "group2")
        .style("fill", function (d, i) { return colorGroup(d); });

    var link2 = svg.selectAll(".link2")
        .data(links2)
      .enter().append("svg:path")
        .attr("class", "link2");

    var reactions = nodes2.filter(function (d, i) {
           if (d.id.indexOf("BiochemicalReaction") >-1)
            return d;
        });
    var proteins = nodes2.filter(function (d, i) {
           if (d.id.indexOf("Protein") >-1)
            return d;
        });
     complexes = nodes2.filter(function (d, i) {
        if (d.id.indexOf("Complex") >-1)
            return d;
    });


    var node2 = svg.selectAll(".node2")
        .data(reactions)
      .enter().append("rect")
        .attr("class", "node2")
        .attr("width", function (d) { return d.width; })
        .attr("height", function (d) { return d.height ; })
        .attr("rx", 1).attr("ry", 1)
        .style("fill", "#000")
        .call(cola.drag);

    var node3 = svg.selectAll(".node3")
        .data(proteins)
      .enter().append("circle")
        .attr("class", "node3")
        .attr("r", function (d) { return d.width/2; })
        .style("fill", function(d) { return getColor("protein"); })
        .call(cola.drag);

     
     
     complexes.forEach(function (d, i) {
        d.children = [];
     });
        
    chart.data().links.componentComplex.forEach(function(l) {
        var id1= d3.select(l.source.node).attr("rdf:ID");  // Participants
        var id2= d3.select(l.target.node).attr("rdf:ID");  // Reactions      
        var cIndex = getComplexById(complexes, id2);
        //console.log("type="+l.source.type+"    id1="+id1+" id2="+id2+"  cIndex="+cIndex);
        if (cIndex>=0){
            var eIndex = getComplexById(complexes[cIndex].children, id1);
            // console.log("type="+l.source.type+"    id1="+id1+" id2="+id2+"  cIndex="+cIndex +"  eIndex="+eIndex);     
            if (eIndex<0){
                complexes[cIndex].children.push( {"id":id1, value:"1" });
            }    
        }    
    });  
        
    function getComplexById(a, id2) {
        if (a){
            for (var i=0; i<a.length;i++){
                var id1 = a[i].id;
                if (id1==id2)
                    return i;
            }
        }
        return -1;
    }
    
    complexes.forEach(function (d1, i) {
        var pack = hierarchy.pack()
        .size([nodeRadius*Math.pow(d1.children.length, 0.6), nodeRadius*Math.pow(d1.children.length,0.6)]);
        var nodes4 = pack.nodes(d1);
        var xx = nodes4[0].x;
        var yy = nodes4[0].y;
        
        var nnn = svg.selectAll(".node5")
          .data(nodes4)
        .enter().append("circle")
          .attr("class", function(d,j) { 
            if (j==0)
                return "node4";
            else
                return "node4"+2; })
          .attr("cx", function(d) { 
            d.width = d.height = nodeRadius*Math.pow(d1.children.length, 0.7);
            d.ddx = d.x - xx;
            d.ddy = d.y - xx;         
            d.pa = d1;         
            return d.x+i*60; })
          .attr("cy", function(d) { return d.y; })
          .attr("r", function(d) { 
            return d.r; })
          .style("fill", function(d,j) { 
             if (j==0)
                return "#800";
            else
                return "#080"; })
          .style("stroke", "#fff")        
          .style("stroke-opacity", 0.5)  
          .style("stroke-width",0.5);
    });

    var label2 = svg.selectAll(".label2")
        .data(nodes2)
       .enter().append("text")
        .attr("class", "label2")
        .attr("dx", "8px")
        .style("text-anchor", "start")
        
        .text(function (d) { return d.name; })
        .call(cola.drag);

   // node2.append("title")
   //     .text(function (d) { return d.id; });

    drawColorLegend();

    cola.on("tick", function () {
        link2.attr('d', function (d) {
                var deltaX = d.target.x - d.source.x,
                    deltaY = d.target.y - d.source.y,
                    dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY),
                    normX = deltaX / dist,
                    normY = deltaY / dist,
                    sourcePadding = d.source.width/2,
                    targetPadding = d.target.width/2,
                    sourceX = d.source.x + (sourcePadding * normX),
                    sourceY = d.source.y + (sourcePadding * normY),
                    targetX = d.target.x - (targetPadding * normX),
                    targetY = d.target.y - (targetPadding * normY);
                return 'M' + sourceX + ',' + sourceY + 'L' + targetX + ',' + targetY;
            });
        //link2.attr("x1", function (d) { return d.source.x2; })
         //   .attr("y1", function (d) { return d.source.y2; })
         //   .attr("x2", function (d) { return d.target.x2; })
         //   .attr("y2", function (d) { return d.target.y2; });
        

        node2.attr("x", function (d) { return d.x - d.width / 2 ; })
            .attr("y", function (d) { return d.y - d.height / 2 ; });
        node3.attr("cx", function (d) { return d.x; })
            .attr("cy", function (d) { return d.y; });
        
        
        var node4 = svg.selectAll(".node4")
        node4.attr("cx", function (d) { return d.x; })
            .attr("cy", function (d) { return d.y; });


        var node42 = svg.selectAll(".node42")
        node42.attr("cx", function (d) { return d.pa.x+d.ddx; })
            .attr("cy", function (d) { return d.pa.y+d.ddy; });
      


        label2.attr("x", function (d) { return d.x; })
             .attr("y", function (d) {
                  var h = this.getBBox().height;
                   return d.y + h/4;
             });
        var condition =  (times<2 && (cola.alpha()<0.02));    
        if (condition){
            makeGroups();
        }  
        if (times>=2){
            group2.attr("x", function (d) { return d.bounds.x; })
                .attr("y", function (d) { return d.bounds.y; })
                .attr("width", function (d) { return d.bounds.width()-3; })
                .attr("height", function (d) { return d.bounds.height()-3; });
              
        }
     //console.log(cola.alpha()+" "+isFirst+"  "+(cola.alpha<0.02)+"  "+condition);   
        
    });


}
function makeGroups(){
    if (times==1){
        console.log("Make GROUPS");     
        groups2.forEach(function(d) {
            d.padding = 10;
        });  

       cola.groups(groups2)
            .start();
    }    
   times++;
}


function update(){
    if (link && node){
            link.each(function (d) {
                if (isIE()) this.parentNode.insertBefore(this, this);
            });
            // draw directed edges with proper padding from node centers
            link.attr('d', function (d) {
                var deltaX = d.target.x - d.source.x,
                    deltaY = d.target.y - d.source.y,
                    dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY),
                    normX = deltaX / dist,
                    normY = deltaY / dist,
                    sourcePadding = nodeRadius/2,
                    targetPadding = nodeRadius/2,
                    sourceX = d.source.x + (sourcePadding * normX),
                    sourceY = d.source.y + (sourcePadding * normY),
                    targetX = d.target.x - (targetPadding * normX),
                    targetY = d.target.y - (targetPadding * normY);
                return 'M' + sourceX + ',' + sourceY + 'L' + targetX + ',' + targetY;
            });

            node.attr("cx", function (d) { return d.x; })
                .attr("cy", function (d) { return d.y; });
          //  nodeText.attr("x", function(d) { return d.x; })
          //  .attr("y", function(d) { return d.y; });
    }    
} 

 function isIE() { return ((navigator.appName == 'Microsoft Internet Explorer') || ((navigator.appName == 'Netscape') && (new RegExp("Trident/.*rv:([0-9]{1,}[\.0-9]{0,})").exec(navigator.userAgent) != null))); }

// check if a node already exist.
function isContainedChild(a, element) {
    if (a){
        for (var i=0; i<a.length;i++){
            var name1 = d3.select(a[i].node).select("displayName");
            var name2 = d3.select(element.node).select("displayName");
            if (name1.text()==name2.text())
                return i;
        }
    }
    return -1;
}

function getReactionById(a, id) {
    if (a){
        for (var i=0; i<a.length;i++){
            var id2 = a[i].id;
            if (id==id2)
                return i;
        }
    }
    return -1;
}

function getNodeById2(a, id2) {
    if (a){
        for (var i=0; i<a.length;i++){
            var id1 = a[i].id;
            if (id1==id2)
                return a[i];
        }
    }
    return undefined;
}

function getGroupById2(a, id2) {
    if (a){
        for (var g=0; g<a.length;g++){
            var group= a[g];
            if (group.leaves){
                for (var i=0; i<group.leaves.length;i++){
                    var nodeIndex = group.leaves[i];
                    var id1 = nodes2[nodeIndex].id;
                    if (id1==id2)
                        return g;
                }
            }
        }
    }
    return undefined;
}

// check if a node already exist.
function getNodeByName(a, name2) {
    if (a){
        for (var i=0; i<a.length;i++){
            var name1 = d3.select(a[i].node).select("displayName");
            if (name1.text()==name2.text())
                return a[i];
        }
    }
    return undefined;
}

  