import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '@features/auth/hooks/useAuth.js';
import BugReportActions from '../components/BugReportActions.jsx';
import BugReportFields from '../components/BugReportFields.jsx';
import BugTagManager from '../components/BugTagManager.jsx';
import { useBugReportForm } from '../hooks/useBugReportForm.js';
import './BugReportPage.css';

function BugReportPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const isEditMode = Boolean(id);
  const bugForm = useBugReportForm({
    bugId: id,
    isEditMode,
    user,
    onSaved: () => navigate('/bugs'),
  });

  if (bugForm.isLoading) {
    return <div className="bug-report-state">Loading report form...</div>;
  }

  return (
    <section className="bug-report-page">
      <header className="bug-report-header">
        <p>Bug reporting</p>
        <h1>{isEditMode ? 'Edit Bug Report' : 'Submit New Bug Report'}</h1>
        <span>Detail the technical issue so the engineering team can resolve it faster.</span>
      </header>

      <form className="bug-report-form" onSubmit={bugForm.submit}>
        <BugReportFields form={bugForm.form} onFieldChange={bugForm.updateField} />

        <BugTagManager
          tags={bugForm.tags}
          selectedTags={bugForm.selectedTags}
          selectedExistingTag={bugForm.selectedExistingTag}
          selectedExistingTagId={bugForm.selectedExistingTagId}
          isTagMenuOpen={bugForm.isTagMenuOpen}
          newTagName={bugForm.newTagName}
          onMenuToggle={bugForm.toggleTagMenu}
          onExistingTagSelect={bugForm.selectExistingTag}
          onExistingTagAdd={bugForm.addExistingTag}
          onNewTagNameChange={bugForm.setNewTagName}
          onNewTagCreate={bugForm.addNewTag}
          onSelectedTagRemove={bugForm.removeSelectedTag}
        />

        {bugForm.errorMessage && <p className="bug-report-message">{bugForm.errorMessage}</p>}

        <BugReportActions
          isEditMode={isEditMode}
          isSaving={bugForm.isSaving}
          onCancel={() => navigate('/bugs')}
        />
      </form>
    </section>
  );
}

export default BugReportPage;
