varying vec2 texCoord;
uniform float m_texturesize;
//varying vec3 vPosition;

uniform sampler2D m_DiffuseMap;

#ifdef NORMALMAP
    uniform sampler2D m_NormalMap;
    uniform float  m_NormalMapPower;    
    varying vec3 mat;
#endif

varying vec3 vNormal;
vec3 diffuseColor;

#ifdef MULTIPLY_COLOR
    uniform vec4 m_Multiply_Color;
#endif

#ifdef FOG
    varying float fog_z;
    uniform vec4 m_FogColor;
    vec4 fogColor;
    float fogFactor;
#endif

#ifdef FOG_SKY
    #import "Common/ShaderLib/Optics.glsllib"
    uniform ENVMAP m_FogSkyBox;
    varying vec3 I;
#endif

void main() {

    vec2 newTexCoord;
    newTexCoord = texCoord;

    #ifdef NORMALMAP
        vec3 normalHeight = texture2D(m_NormalMap, newTexCoord).rgb;
        //vec3 normal = ((normalHeight.xyz  - vec3(0.5))* vec3(2.0));
        vec3 normal = (normalHeight.xyz * vec3(2.0) - vec3(1.0));
        normal = normalize(normal);

        #if defined (NOR_INV_X) && (NORMALMAP) 
            normal.x = -normal.x;
        #endif

        #if defined (NOR_INV_Y) && (NORMALMAP)
            normal.y = -normal.y;
        #endif

        #if defined (NOR_INV_Z) && (NORMALMAP)
            normal.z = -normal.z;
        #endif
 
    #else 
        vec3 normal = vNormal;
    #endif

    //vec3  vmr = vNormal.xyz;
    //vec3 coords = (vmr);
    vec3 coords = vNormal;

    #if defined (NORMALMAP)
        vec3  normalz = mat.xyz * normal.xyz;
        diffuseColor = texture2D(m_DiffuseMap, vec2(((coords.xyz + normalz.xyz* vec3(m_NormalMapPower)) * vec3(0.495) + vec3(0.5)))).rgb;
    #else
        diffuseColor = texture2D(m_DiffuseMap, vec2(coords.xyz * vec3(0.495) + vec3(0.5))).rgb;
        //diffuseColor = (diffuseColor - vec3(0.5, 0.5, 0.5) * 2.0);
    #endif
    
    #ifdef MULTIPLY_COLOR
        diffuseColor.rgb *= m_Multiply_Color.rgb;
    #endif

    gl_FragColor.rgb = diffuseColor;
    gl_FragColor.a = 1.0;

    #ifdef FOG

        fogColor = m_FogColor;

        #ifdef FOG_SKY
            fogColor.rgb = Optics_GetEnvColor(m_FogSkyBox, I).rgb;
        #endif

        float fogDistance = fogColor.a;
        float depth = (fog_z - fogDistance) / fogDistance;
        depth = max(depth, 0.0);
        fogFactor = exp2(-depth * depth);
        fogFactor = clamp(fogFactor, 0.05, 1.0);

        gl_FragColor.rgb = mix(fogColor.rgb, gl_FragColor.rgb, vec3(fogFactor));

     #endif
}
