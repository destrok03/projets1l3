// ===== Site JavaScript =====

$(document).ready(function() {
    
    // ===== Mise à jour du compteur panier =====
    function updatePanierCount() {
        $.get('/panier/count', function(data) {
            $('#panierCount').text(data.count);
        });
    }
    
    // ===== Ajouter au panier via AJAX =====
    $(document).on('submit', '.btn-ajouter-panier', function(e) {
        var form = $(this).closest('form');
        
        // Animation du bouton
        var btn = $(this);
        var originalText = btn.html();
        btn.html('<i class="fas fa-spinner fa-spin me-1"></i>Ajout...');
        btn.prop('disabled', true);
        
        $.ajax({
            url: form.attr('action'),
            method: 'POST',
            data: form.serialize(),
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
            success: function(response) {
                if (response.success) {
                    // Mettre à jour le compteur
                    $('#panierCount').text(response.nombreItems);
                    
                    // Afficher notification
                    showToast('success', response.message);
                    
                    // Animation du compteur
                    $('#panierCount').addClass('animate__animated animate__bounce');
                    setTimeout(function() {
                        $('#panierCount').removeClass('animate__animated animate__bounce');
                    }, 1000);
                }
            },
            error: function() {
                showToast('error', 'Erreur lors de l\'ajout au panier');
            },
            complete: function() {
                btn.html(originalText);
                btn.prop('disabled', false);
            }
        });
        
        e.preventDefault();
    });
    
    // ===== Toast Notification =====
    function showToast(type, message) {
        var bgClass = type === 'success' ? 'bg-success' : 'bg-danger';
        var icon = type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle';
        
        var toast = $(`
            <div class="toast align-items-center text-white ${bgClass} border-0" role="alert">
                <div class="d-flex">
                    <div class="toast-body">
                        <i class="fas ${icon} me-2"></i>${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>
        `);
        
        // Créer le conteneur s'il n'existe pas
        if ($('.toast-container').length === 0) {
            $('body').append('<div class="toast-container"></div>');
        }
        
        $('.toast-container').append(toast);
        
        var bsToast = new bootstrap.Toast(toast[0], { delay: 3000 });
        bsToast.show();
        
        // Supprimer après fermeture
        toast.on('hidden.bs.toast', function() {
            $(this).remove();
        });
    }
    
    // ===== Auto-dismiss alerts =====
    setTimeout(function() {
        $('.alert-dismissible').fadeOut('slow', function() {
            $(this).remove();
        });
    }, 5000);
    
    // ===== Smooth scroll =====
    $('a[href^="#"]').on('click', function(e) {
        var target = $(this.getAttribute('href'));
        if (target.length) {
            e.preventDefault();
            $('html, body').animate({
                scrollTop: target.offset().top - 80
            }, 500);
        }
    });
    
    // ===== Format prix =====
    function formatPrix(prix) {
        return prix.toLocaleString('fr-FR') + ' FCFA';
    }
    
    // ===== Validation téléphone Sénégal =====
    $('input[name*="Telephone"]').on('input', function() {
        var val = $(this).val().replace(/\D/g, '');
        if (val.length > 9) {
            val = val.substring(0, 9);
        }
        $(this).val(val);
    });
    
});

// ===== Confirmation de suppression =====
function confirmDelete(message) {
    return confirm(message || 'Êtes-vous sûr de vouloir supprimer cet élément ?');
}
