package usach.cl.laboratorio1.controller;

import usach.cl.laboratorio1.dto.InventarioDTO;
import usach.cl.laboratorio1.dto.InventarioItemDTO;
import usach.cl.laboratorio1.repository.InventarioItemRepository;
import usach.cl.laboratorio1.repository.InventarioRepository;
import usach.cl.laboratorio1.service.InventarioItemService;
import usach.cl.laboratorio1.service.InventarioService;
import usach.cl.laboratorio1.tablas.Inventario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventarios")
@CrossOrigin("*")
public class InventarioController {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private InventarioItemRepository inventarioItemRepository;

    @Autowired
    private InventarioItemService inventarioItemService;

    @GetMapping
    public List<InventarioDTO> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return inventarioRepository.findAllDetalle(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioDTO> findById(@PathVariable Integer id) {
        InventarioDTO e = inventarioRepository.findDetalleById(id);
        return e != null ? ResponseEntity.ok(e) : ResponseEntity.notFound().build();
    }

    @GetMapping("/por-personaje/{idPersonaje}")
    public ResponseEntity<InventarioDTO> findByPersonaje(@PathVariable Integer idPersonaje) {
        InventarioDTO e = inventarioRepository.findDetalleByPersonaje(idPersonaje);
        return e != null ? ResponseEntity.ok(e) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/items")
    public List<InventarioItemDTO> itemsByInventario(@PathVariable Integer id) {
        return inventarioItemRepository.findDetalleByInventario(id);
    }

    @GetMapping("/por-personaje/{idPersonaje}/items")
    public List<InventarioItemDTO> itemsByPersonaje(@PathVariable Integer idPersonaje) {
        return inventarioItemRepository.findDetalleByPersonaje(idPersonaje);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Inventario inventario) {
        try {
            inventarioService.prepararInventario(inventario);
            inventarioRepository.save(inventario);
            return ResponseEntity.ok("Inventario creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id,
                                    @RequestBody Inventario inventario) {
        inventario.setIdInventario(id);
        inventarioService.prepararInventario(inventario);
        inventarioRepository.update(inventario);
        return ResponseEntity.ok("Inventario actualizado");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        inventarioRepository.deleteById(id);
        return ResponseEntity.ok("Inventario eliminado");
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<?> addItem(@PathVariable Integer id,
                                     @RequestBody InventarioItemRequest request) {
        try {
            inventarioItemService.agregarItem(id, request.idItem);
            return ResponseEntity.ok("Item agregado al inventario");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/items/{idItem}")
    public ResponseEntity<?> removeItem(@PathVariable Integer id,
                                        @PathVariable Integer idItem) {
        inventarioItemService.eliminarItem(id, idItem);
        return ResponseEntity.ok("Item eliminado del inventario");
    }

    static class InventarioItemRequest {
        public Integer idItem;
    }
}
