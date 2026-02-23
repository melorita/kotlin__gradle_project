package org.dbu.library.service

import org.dbu.library.model.Book
import org.dbu.library.repository.LibraryRepository
import org.dbu.library.service.BorrowResult

class DefaultLibraryService(
    private val repository: LibraryRepository
) : LibraryService {

    override fun addBook(book: Book): Boolean {
        return repository.addBook(book)
    }

    override fun borrowBook(patronId: String, isbn: String): BorrowResult {
        val patron = repository.findPatron(patronId) ?: return BorrowResult.PATRON_NOT_FOUND
        val book = repository.findBook(isbn) ?: return BorrowResult.BOOK_NOT_FOUND

        if (!book.isAvailable) return BorrowResult.NOT_AVAILABLE
        if (patron.borrowedBooks.size >= 3) return BorrowResult.LIMIT_REACHED

        val updatedBook = book.copy(isAvailable = false)
        val updatedPatron = patron.copy(borrowedBooks = patron.borrowedBooks + isbn)

        repository.updateBook(updatedBook)
        repository.updatePatron(updatedPatron)

        return BorrowResult.SUCCESS
    }

    override fun returnBook(patronId: String, isbn: String): Boolean {
        val patron = repository.findPatron(patronId) ?: return false
        val book = repository.findBook(isbn) ?: return false

        if (!patron.borrowedBooks.contains(isbn)) return false

        val updatedBook = book.copy(isAvailable = true)
        val updatedPatron = patron.copy(borrowedBooks = patron.borrowedBooks - isbn)

        repository.updateBook(updatedBook)
        repository.updatePatron(updatedPatron)

        return true
    }

    override fun search(query: String): List<Book> {
        return repository.getAllBooks().filter {
            it.title.contains(query, ignoreCase = true) ||
            it.author.contains(query, ignoreCase = true)
        }
    }
}