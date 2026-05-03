package usach.cl.laboratorio1.controller;

import usach.cl.laboratorio1.repository.ItemRepository;
import usach.cl.laboratorio1.tablas.HistorialBotin;
import usach.cl.laboratorio1.tablas.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
@CrossOrigin("*")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    // GET /api/items - Listar todos los items
    @GetMapping
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    // GET /api/items/1 - Obtener item por ID
    @GetMapping("/{id}")
    public ResponseEntity<Item> findById(@PathVariable Integer id) {
        Item item = itemRepository.findById(id);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    // POST /api/items - Crear un item nuevo
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Item item) {
        try {
            itemRepository.save(item);
            return ResponseEntity.ok("Item creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // PUT /api/items/1 - Actualizar un item
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Item item) {
        item.setIdItem(id);
        itemRepository.update(item);
        return ResponseEntity.ok("Item actualizado");
    }

    // DELETE /api/items/1 - Eliminar un item
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        itemRepository.deleteById(id);
        return ResponseEntity.ok("Item eliminado");
    }

    // POST /api/items/repartir
    // Requerimiento 3: Stored Procedure 1 - distribuir botin.
    // Ejecuta sp_repartir_botin que en una transaccion atomica:
    // verifica DKP, descuenta puntos, registra en historial.
    @PostMapping("/repartir")
    public ResponseEntity<?> repartir(@RequestBody BotinRequest request) {
        try {
            itemRepository.repartirBotin(request.idPersonaje, request.idItem, request.idRaid);
            return ResponseEntity.ok("Botin asignado y DKP descontados.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/items/historial/1
    // Requerimiento 10: historial de botin de un jugador especifico.
    @GetMapping("/historial/{idPersonaje}")
    public List<HistorialBotin> historialBotin(@PathVariable Integer idPersonaje) {
        return itemRepository.obtenerHistorialPorPersonaje(idPersonaje);
    }

    // GET /api/items/ranking
    // Requerimiento 7: ranking del clan desde la vista materializada.
    @GetMapping("/ranking")
    public List<Map<String, Object>> ranking() {
        return itemRepository.obtenerRanking();
    }

    // POST /api/items/ranking/refrescar
    // Requerimiento 7: refrescar la vista materializada manualmente.
    @PostMapping("/ranking/refrescar")
    public ResponseEntity<?> refrescarRanking() {
        itemRepository.refrescarRanking();
        return ResponseEntity.ok("Ranking actualizado");
    }

    static class BotinRequest {
        public Integer idPersonaje;
        public Integer idItem;
        public Integer idRaid;
    }
}