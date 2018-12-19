varying float fog_z;
uniform float m_Distance;

#ifdef MASK_TEXTURE
uniform sampler2D m_MaskTex;
#endif

void main() {

#ifdef DO_REFRACT

    // red is fog factor and green is mask.
    gl_FragColor = vec4(1.0);
    float fogDistance = m_Distance;
    float depth = (fog_z - fogDistance) / fogDistance;
    depth = max(depth, 0.0);
    float fogFactor = exp2(-depth * depth);
    fogFactor = clamp(fogFactor, 0.05, 1.0);
    gl_FragColor.r = mix(0.0, gl_FragColor.r, fogFactor);

    #ifdef MASK_TEXTURE
    float mask = texture2D(m_MaskTex, texCoord.xy).r;
    gl_FragColor.g *= mask;
    #endif

#else
gl_FragColor = vec4(0.0);
#endif
}