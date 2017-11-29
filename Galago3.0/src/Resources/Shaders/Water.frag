varying vec2 texCoord;

uniform float m_Speed;
uniform vec2 m_Scale;
uniform vec4 m_StartColor;
uniform vec4 m_EndColor;
uniform float g_Time;

#define time g_Time*m_Speed

vec2 hash2(vec2 p ) {
   return fract(sin(vec2(dot(p, vec2(123.4, 748.6)), dot(p, vec2(547.3, 659.3))))*5232.85324);   
}
float hash(vec2 p) {
  return fract(sin(dot(p, vec2(43.232, 75.876)))*4526.3257);   
}

//Based off of iq's described here: http://www.iquilezles.org/www/articles/voronoilin
float voronoi(vec2 p) {
    vec2 n = floor(p);
    vec2 f = fract(p);
    float md = 5.0;
    vec2 m = vec2(0.0);
    for (int i = -1;i<=1;i++) {
        for (int j = -1;j<=1;j++) {
            vec2 g = vec2(i, j);
            vec2 o = hash2(n+g);
            o = 0.5+0.5*sin(time+5.038*o.xy);
            vec2 r = g + o - f;
            float d = dot(r, r);
            if (d<md) {
              md = d;
              m = n+g+o;
            }
        }
    }
    return md;
}

float ov(vec2 p) {
    float v = 0.0;
    float a = 0.4;
    for (int i = 0;i<3;i++) {
        v+= voronoi(p)*a;
        p*=2.0;
        a*=0.5;
    }
    return v;
}


void main() {
    vec2 res = m_Scale;	
    vec2 uv = texCoord.xy / res.xy;
    vec4 a = m_StartColor;//vec4(0.2, 0.4, 1.0, 1.0);
    vec4 b = m_EndColor; //vec4(0.85, 0.9, 1.0, 1.0);
    gl_FragColor = vec4(mix(a, b, smoothstep(0.0, 0.5, ov(uv*20.0))));

}