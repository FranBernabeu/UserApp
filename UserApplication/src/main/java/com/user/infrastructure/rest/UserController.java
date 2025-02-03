package com.user.infrastructure.rest;

import com.user.domain.User;
import com.user.application.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Retorna todos los usuarios paginados",
            tags = {"Usuarios"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios encontrados",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = User.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No hay usuarios registrados",
                    content = @Content
            )
    })
    @GetMapping
    public Page<User> getAllUsers(@ParameterObject Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @Operation(
            summary = "Obtener usuario por username",
            description = "Busca un usuario por su identificador único",
            tags = {"Usuarios"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content
            )
    })
    @GetMapping("/{username}")
    public User getUser(
            @Parameter(description = "Username del usuario", example = "johndoe123")
            @PathVariable String username
    ) {
        return userService.getUserByUsername(username);
    }

    @Operation(
            summary = "Crear nuevo usuario",
            description = "Registra un nuevo usuario en el sistema",
            tags = {"Usuarios"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto - Username ya existe",
                    content = @Content
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del usuario a crear",
                    required = true
            )
            @RequestBody User user
    ) {
        return userService.createUser(user);
    }

    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza los datos de un usuario existente",
            tags = {"Usuarios"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content
            )
    })
    @PutMapping("/{username}")
    public User updateUser(
            @Parameter(description = "Username del usuario a actualizar", example = "johndoe123")
            @PathVariable String username,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del usuario",
                    required = true
            )
            @RequestBody User user
    ) {
        return userService.updateUser(username, user);
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario del sistema",
            tags = {"Usuarios"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario eliminado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content
            )
    })
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @Parameter(description = "Username del usuario a eliminar", example = "johndoe123")
            @PathVariable String username
    ) {
        userService.deleteUser(username);
    }

    @Operation(
            summary = "Generar usuarios aleatorios",
            description = "Crea usuarios aleatorios usando Random User Generator API",
            tags = {"Usuarios"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuarios generados exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Número inválido de usuarios solicitados",
                    content = @Content
            )
    })
    @GetMapping("/generate/{number}")
    public Iterable<User> generateUsers(
            @Parameter(description = "Número de usuarios a generar", example = "5")
            @PathVariable int number
    ) {
        return userService.generateRandomUsers(number);
    }

    @Operation(
            summary = "Obtener árbol de usuarios",
            description = "Estructura jerárquica de usuarios agrupados por País > Estado > Ciudad",
            tags = {"Usuarios"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Estructura del árbol de usuarios",
            content = @Content(schema = @Schema(
                    example = """
            {
                "Spain": {
                    "Madrid": {
                        "Madrid": [ ... ],
                        "Alcalá de Henares": [ ... ]
                    }
                },
                "USA": {
                    "California": {
                        "Los Angeles": [ ... ]
                    }
                }
            }"""
            ))
    )
    @GetMapping("/tree")
    public Map<String, Map<String, Map<String, List<User>>>> getTree() {
        return userService.getUsersTree();
    }
}