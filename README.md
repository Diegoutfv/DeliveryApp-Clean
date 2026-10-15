# 🍕 Delivery App

Aplicación Android de delivery desarrollada con **Kotlin**, **Jetpack Compose** y **Clean Architecture**.

---

## 📱 Descripción

App móvil que permite a los usuarios explorar un menú de platillos, agregarlos a un carrito de compras, modificar cantidades y confirmar la orden. Los datos se persisten localmente con **Room** y la inyección de dependencias se gestiona con **Hilt**.

---

## ✨ Funcionalidades

- ✅ Ver catálogo de platillos con nombre, descripción y precio
- ✅ Agregar platillos al carrito de compras
- ✅ Modificar cantidades (`+` / `−`) en el carrito
- ✅ Eliminar items individuales
- ✅ Cálculo automático del total en tiempo real
- ✅ Persistencia local con Room
- ✅ Confirmación visual con Snackbars
- ✅ Estado reactivo con StateFlow + Compose

---

## 🏗️ Arquitectura

El proyecto sigue los principios de **Clean Architecture** dividida en capas:
