# Bitácora de uso de asistentes de IA

Ejercicio: POO-06 — Revisión, paquetes, constructores y sobrecarga
(`ejercicio-poo-06-revision-paquetes-constructores-2026-09-30`).

Esta bitácora registra qué asistente de IA se usó, con qué modelo exacto y para
qué. Las decisiones de diseño y el código entregado son responsabilidad de la
autora; el asistente se usó como revisor y redactor de apoyo.

## Sesiones

| Fecha | Asistente (marca) | Modelo de LLM exacto | Para qué |
|---|---|---|---|
|2026-09-30 | Claude (Anthropic) | Claude Opus 5.5 (`claude-opus-5-5`)  | Trabajo de la rama `poo-06-mejoras` del 30/09/2026 (refactor de paquetes, invariantes, sobrecarga, errores 400, README)|
| 2026-10-04 | Claude (Anthropic) | Claude Opus 5.5 (`claude-opus-5-5`) | Revisión de la rama `poo-06-mejoras` contra el enunciado y la rúbrica antes del merge a `main`; redacción de esta bitácora y de la sección del README sobre sobrecarga y sobreescritura. |

## Resumen de prompts — sesión del 2026-10-04 (Claude Opus 5.5)

1. **Verificación previa al merge.** Se pidió verificar que el repositorio
   `ii-taller-git-2026` cumpla el enunciado POO-06, la rúbrica de la cátedra
   (ocho criterios) y el template de paquetes, para poder hacer merge a `main`.
   - El asistente revisó el código, el historial de Git y el README.
   - Comparó el diagrama Mermaid con el código, método por método.
   - Compiló el dominio y probó los invariantes: constructor sobrecargado,
     rechazo de `NaN` y de salud 0, desbordes de `curar` y de `ganarExperiencia`,
     y tope de velocidad.
   - No pudo ejecutar Maven: el entorno del asistente no accede a Maven
     Central. Los tests y `./mvnw spring-boot:run` se verifican localmente.
2. **Aclaración de la rama.** Se indicó que la rama a mergear es
   `poo-06-mejoras`. El asistente confirmó que el merge es *fast-forward* y sin
   conflictos, y listó lo que faltaba: esta bitácora, la mención de la licencia
   en el README, un apartado sobre sobrecarga y sobreescritura, el enlace al
   commit y el archivo de especificaciones para Classroom.
3. **Redacción.** Se pidió crear esta bitácora y la sección del README
   «Sobrecarga y sobreescritura: qué cambió». También se agregaron al README la
   licencia, el lugar para el enlace al commit y una línea que justifica los
   nombres de los paquetes.

## Qué no hizo el asistente

- No escribió código del dominio ni de la API en esta sesión.
- No hizo push ni merge en el repositorio.
